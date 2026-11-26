package io.github.hello09x.fakeplayer.core.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class FakeChannelPipeline implements ChannelPipeline {
   private final Channel channel;

   public FakeChannelPipeline(Channel channel) {
      this.channel = channel;
   }

   public ChannelPipeline addFirst(String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addLast(String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      return this;
   }

   public ChannelPipeline addFirst(ChannelHandler... handlers) {
      return this;
   }

   public ChannelPipeline addFirst(EventExecutorGroup group, ChannelHandler... handlers) {
      return this;
   }

   public ChannelPipeline addLast(ChannelHandler... handlers) {
      return this;
   }

   public ChannelPipeline addLast(EventExecutorGroup group, ChannelHandler... handlers) {
      return this;
   }

   public ChannelPipeline remove(ChannelHandler handler) {
      return this;
   }

   public ChannelHandler remove(String name) {
      return null;
   }

   public <T extends ChannelHandler> T remove(Class<T> handlerType) {
      return null;
   }

   public ChannelHandler removeFirst() {
      return null;
   }

   public ChannelHandler removeLast() {
      return null;
   }

   public ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
      return this;
   }

   public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
      return null;
   }

   public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
      return null;
   }

   public ChannelHandler first() {
      return null;
   }

   public ChannelHandlerContext firstContext() {
      return null;
   }

   public ChannelHandler last() {
      return null;
   }

   public ChannelHandlerContext lastContext() {
      return null;
   }

   public ChannelHandler get(String name) {
      return null;
   }

   public <T extends ChannelHandler> T get(Class<T> handlerType) {
      return null;
   }

   public ChannelHandlerContext context(ChannelHandler handler) {
      return null;
   }

   public ChannelHandlerContext context(String name) {
      return null;
   }

   public ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
      return null;
   }

   public Channel channel() {
      return this.channel;
   }

   public List<String> names() {
      return List.of();
   }

   public Map<String, ChannelHandler> toMap() {
      return Map.of();
   }

   public ChannelPipeline fireChannelRegistered() {
      return this;
   }

   public ChannelPipeline fireChannelUnregistered() {
      return this;
   }

   public ChannelPipeline fireChannelActive() {
      return this;
   }

   public ChannelPipeline fireChannelInactive() {
      return this;
   }

   public ChannelPipeline fireExceptionCaught(Throwable cause) {
      return this;
   }

   public ChannelPipeline fireUserEventTriggered(Object event) {
      return this;
   }

   public ChannelPipeline fireChannelRead(Object msg) {
      ReferenceCountUtil.release(msg);
      return this;
   }

   public ChannelPipeline fireChannelReadComplete() {
      return this;
   }

   public ChannelPipeline fireChannelWritabilityChanged() {
      return this;
   }

   public ChannelFuture bind(SocketAddress localAddress) {
      return this.newSucceededFuture();
   }

   public ChannelFuture connect(SocketAddress remoteAddress) {
      return this.newSucceededFuture();
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.newSucceededFuture();
   }

   public ChannelFuture disconnect() {
      return this.newSucceededFuture();
   }

   public ChannelFuture close() {
      return this.newSucceededFuture();
   }

   public ChannelFuture deregister() {
      return this.newSucceededFuture();
   }

   public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture disconnect(ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture close(ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture deregister(ChannelPromise promise) {
      promise.setSuccess();
      return promise;
   }

   public ChannelOutboundInvoker read() {
      return null;
   }

   public ChannelFuture write(Object msg) {
      ReferenceCountUtil.release(msg);
      return this.newSucceededFuture();
   }

   public ChannelFuture write(Object msg, ChannelPromise promise) {
      ReferenceCountUtil.release(msg);
      promise.setSuccess();
      return promise;
   }

   public ChannelPipeline flush() {
      return this;
   }

   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      ReferenceCountUtil.release(msg);
      promise.setSuccess();
      return promise;
   }

   public ChannelFuture writeAndFlush(Object msg) {
      ReferenceCountUtil.release(msg);
      return this.newSucceededFuture();
   }

   public ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel);
   }

   public ChannelProgressivePromise newProgressivePromise() {
      return null;
   }

   public ChannelFuture newSucceededFuture() {
      DefaultChannelPromise promise = new DefaultChannelPromise(this.channel);
      promise.setSuccess(null);
      return promise;
   }

   public ChannelFuture newFailedFuture(Throwable cause) {
      DefaultChannelPromise promise = new DefaultChannelPromise(this.channel);
      promise.setFailure(cause);
      return promise;
   }

   public ChannelPromise voidPromise() {
      DefaultChannelPromise promise = new DefaultChannelPromise(this.channel);
      promise.setSuccess(null);
      return promise;
   }

   @NotNull
   public Iterator<Entry<String, ChannelHandler>> iterator() {
      return Collections.<String, ChannelHandler>emptyMap().entrySet().iterator();
   }
}
