package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface CycleDetectingLock<ID> {

    ListMultimap<Thread, ID> lockOrDetectPotentialLocksCycle();

    void unlock();

    public static class CycleDetectingLockFactory<ID> {

        private static Map<Thread, CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?>> lockThreadIsWaitingOn = Maps.newHashMap();
        private static final Multimap<Thread, CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?>> locksOwnedByThread = LinkedHashMultimap.create();

        public CycleDetectingLockFactory() {
            super();
        }

        CycleDetectingLock<ID> create(ID userLockId) {
            return new CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<ID>(this, userLockId, new ReentrantLock());
        }

        static class ReentrantCycleDetectingLock<ID> implements CycleDetectingLock<ID> {

            private final Lock lockImplementation;
            private final ID userLockId;
            private final CycleDetectingLock.CycleDetectingLockFactory<ID> lockFactory;
            private Thread lockOwnerThread = null;
            private int lockReentranceCount = 0;

            ReentrantCycleDetectingLock(CycleDetectingLock.CycleDetectingLockFactory<ID> lockFactory, ID userLockId, Lock lockImplementation) {
                super();
                this.lockFactory = lockFactory;
                this.userLockId = (ID) Preconditions.checkNotNull(userLockId, "userLockId");
                this.lockImplementation = (Lock) Preconditions.checkNotNull(lockImplementation, "lockImplementation");
            }

            public ListMultimap<Thread, ID> lockOrDetectPotentialLocksCycle() {
                Thread currentThread = Thread.currentThread();

                synchronized (CycleDetectingLock.CycleDetectingLockFactory.class) {
                    this.checkState();
                    if (this.lockOwnerThread != currentThread) {
                        CycleDetectingLock.CycleDetectingLockFactory.lockThreadIsWaitingOn.put(currentThread, this);
                        ListMultimap<Thread, ID> locksInCycle = this.detectPotentialLocksCycle();

                        if (!locksInCycle.isEmpty()) {
                            CycleDetectingLock.CycleDetectingLockFactory.lockThreadIsWaitingOn.remove(currentThread);
                            return locksInCycle;
                        }
                    }
                }

                this.lockImplementation.lock();
                synchronized (CycleDetectingLock.CycleDetectingLockFactory.class) {
                    CycleDetectingLock.CycleDetectingLockFactory.lockThreadIsWaitingOn.remove(currentThread);
                    this.checkState();
                    this.lockOwnerThread = currentThread;
                    ++this.lockReentranceCount;
                    CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.put(currentThread, this);
                }

                return ImmutableListMultimap.of();
            }

            public void unlock() {
                Thread currentThread = Thread.currentThread();

                synchronized (CycleDetectingLock.CycleDetectingLockFactory.class) {
                    this.checkState();
                    Preconditions.checkState(this.lockOwnerThread != null, "Thread is trying to unlock a lock that is not locked");
                    Preconditions.checkState(this.lockOwnerThread == currentThread, "Thread is trying to unlock a lock owned by another thread");
                    this.lockImplementation.unlock();
                    --this.lockReentranceCount;
                    if (this.lockReentranceCount == 0) {
                        this.lockOwnerThread = null;
                        Preconditions.checkState(CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.remove(currentThread, this), "Internal error: Can not find this lock in locks owned by a current thread");
                        if (CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.get(currentThread).isEmpty()) {
                            CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.removeAll(currentThread);
                        }
                    }

                }
            }

            void checkState() throws IllegalStateException {
                Thread currentThread = Thread.currentThread();

                Preconditions.checkState(!CycleDetectingLock.CycleDetectingLockFactory.lockThreadIsWaitingOn.containsKey(currentThread), "Internal error: Thread should not be in a waiting thread on a lock now");
                if (this.lockOwnerThread != null) {
                    Preconditions.checkState(this.lockReentranceCount >= 0, "Internal error: Lock ownership and reentrance count internal states do not match");
                    Preconditions.checkState(CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.get(this.lockOwnerThread).contains(this), "Internal error: Set of locks owned by a current thread and lock ownership status do not match");
                } else {
                    Preconditions.checkState(this.lockReentranceCount == 0, "Internal error: Reentrance count of a non locked lock is expect to be zero");
                    Preconditions.checkState(!CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.values().contains(this), "Internal error: Non locked lock should not be owned by any thread");
                }

            }

            private ListMultimap<Thread, ID> detectPotentialLocksCycle() {
                Thread currentThread = Thread.currentThread();

                if (this.lockOwnerThread != null && this.lockOwnerThread != currentThread) {
                    ListMultimap<Thread, ID> potentialLocksCycle = MultimapBuilder.linkedHashKeys().arrayListValues().build();
                    CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?> lockOwnerWaitingOn = this;

                    while (lockOwnerWaitingOn != null && lockOwnerWaitingOn.lockOwnerThread != null) {
                        Thread threadOwnerThreadWaits = lockOwnerWaitingOn.lockOwnerThread;

                        lockOwnerWaitingOn = this.addAllLockIdsAfter(threadOwnerThreadWaits, lockOwnerWaitingOn, potentialLocksCycle);
                        if (threadOwnerThreadWaits == currentThread) {
                            return potentialLocksCycle;
                        }
                    }

                    return ImmutableListMultimap.of();
                } else {
                    return ImmutableListMultimap.of();
                }
            }

            private CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?> addAllLockIdsAfter(Thread thread, CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?> lock, ListMultimap<Thread, ID> potentialLocksCycle) {
                boolean found = false;
                Collection<CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?>> ownedLocks = CycleDetectingLock.CycleDetectingLockFactory.locksOwnedByThread.get(thread);

                Preconditions.checkNotNull(ownedLocks, "Internal error: No locks were found taken by a thread");

                for (CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?> ownedLock : ownedLocks) {
                    if (ownedLock == lock) {
                        found = true;
                    }

                    if (found && ownedLock.lockFactory == this.lockFactory) {
                        ID userLockId = ownedLock.userLockId;

                        potentialLocksCycle.put(thread, userLockId);
                    }
                }

                Preconditions.checkState(found, "Internal error: We can not find locks that created a cycle that we detected");
                CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock<?> unownedLock = (CycleDetectingLock.CycleDetectingLockFactory.ReentrantCycleDetectingLock) CycleDetectingLock.CycleDetectingLockFactory.lockThreadIsWaitingOn.get(thread);

                if (unownedLock != null && unownedLock.lockFactory == this.lockFactory) {
                    ID typed = unownedLock.userLockId;

                    potentialLocksCycle.put(thread, typed);
                }

                return unownedLock;
            }

            public String toString() {
                Thread thread = this.lockOwnerThread;

                return thread != null ? String.format("%s[%s][locked by %s]", super.toString(), this.userLockId, thread) : String.format("%s[%s][unlocked]", super.toString(), this.userLockId);
            }
        }
    }
}
