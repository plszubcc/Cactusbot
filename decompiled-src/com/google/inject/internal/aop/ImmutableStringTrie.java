package com.google.inject.internal.aop;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToIntFunction;

final class ImmutableStringTrie implements ToIntFunction<String> {

    private static final char LEAF_MARKER = '耀';
    private static final char BUD_MARKER = '䀀';
    private static final int MAX_ROWS_PER_TRIE = 16384;
    private final char[] trie;

    private static int singletonTrie(String key) {
        return 0;
    }

    public int applyAsInt(String key) {
        int keyLength = key.length();
        int keyIndex = 0;
        int dataIndex = 0;

        int branchCount;

        for (char[] data = this.trie; keyIndex < keyLength; dataIndex += branchCount * 3 - 1) {
            branchCount = data[dataIndex++];
            int branchIndex = Arrays.binarySearch(data, dataIndex, dataIndex + branchCount, key.charAt(keyIndex));

            if (branchIndex < 0) {
                break;
            }

            int resultIndex = branchIndex + branchCount;
            char result = data[resultIndex];

            if ((result & '耀') != 0) {
                return result & -32769;
            }

            if ((result & 16384) != 0) {
                if (keyIndex == keyLength - 1) {
                    return result & -16385;
                }

                result = 1;
            }

            keyIndex += result;
            if (branchIndex > dataIndex) {
                int jumpIndex = resultIndex + branchCount - 1;

                dataIndex += data[jumpIndex];
            }
        }

        return -1;
    }

    public static ToIntFunction<String> buildTrie(Collection<String> table) {
        int numRows = table.size();

        return numRows > 1 ? buildTrie(new StringBuilder(), (String[]) table.toArray(new String[numRows]), 0, numRows) : ImmutableStringTrie::singletonTrie;
    }

    private static ToIntFunction<String> buildTrie(StringBuilder buf, String[] table, int row, int rowLimit) {
        int trieLimit = row + 16384;

        if (rowLimit <= trieLimit) {
            buildSubTrie(buf, table, 0, row, rowLimit);
            char[] data = new char[buf.length()];

            buf.getChars(0, data.length, data, 0);
            return new ImmutableStringTrie(data);
        } else {
            buildSubTrie(buf, table, 0, row, trieLimit);
            char[] data = new char[buf.length()];

            buf.getChars(0, data.length, data, 0);
            buf.setLength(0);
            return new ImmutableStringTrie.Overflow(data, table[trieLimit], buildTrie(buf, table, trieLimit, rowLimit));
        }
    }

    ImmutableStringTrie(char[] data) {
        super();
        this.trie = data;
    }

    private static void buildSubTrie(StringBuilder buf, String[] table, int column, int row, int rowLimit) {
        int trieStart = buf.length();
        int prevRow = row;
        int branchCount = 0;
        int nextJump = 0;

        boolean allLeaves;

        for (allLeaves = true; prevRow < rowLimit; ++branchCount) {
            String cells = table[prevRow];
            int columnLimit = cells.length();
            char pivot = cells.charAt(column);
            int nextRow = nextPivotRow(table, pivot, column, prevRow, rowLimit);
            int nextColumn = nextPivotColumn(table, column, prevRow, nextRow);

            if (nextColumn == columnLimit && nextColumn - column > 1 && nextRow - prevRow > 1) {
                --nextColumn;
            }

            int branchIndex = trieStart + branchCount;

            buf.insert(branchIndex, pivot);
            int resultIndex = branchIndex + 1 + branchCount;
            int subTrieStart = buf.length() + 1;

            if (nextColumn < columnLimit) {
                buf.insert(resultIndex, (char) (nextColumn - column));
                buildSubTrie(buf, table, nextColumn, prevRow, nextRow);
                allLeaves = false;
            } else {
                buildSubTrie(buf, table, nextColumn, prevRow + 1, nextRow);
                boolean isLeaf = subTrieStart > buf.length();
                char marker = (char) (isLeaf ? '耀' : 16384);

                buf.insert(resultIndex, (char) (prevRow & 16383 | marker));
                allLeaves = allLeaves && isLeaf;
            }

            if (nextRow < rowLimit) {
                int jumpIndex = resultIndex + 1 + branchCount;

                nextJump += buf.length() - subTrieStart;
                buf.insert(jumpIndex, (char) nextJump);
            }

            prevRow = nextRow;
        }

        if (branchCount > 0) {
            buf.insert(trieStart, (char) branchCount);
            if (allLeaves) {
                int jumpStart = trieStart + 1 + branchCount * 2;

                buf.delete(jumpStart, jumpStart + branchCount);
            }
        }

    }

    private static int nextPivotRow(String[] table, char pivot, int column, int row, int rowLimit) {
        for (int r = row + 1; r < rowLimit; ++r) {
            String cells = table[r];

            if (cells.length() <= column || cells.charAt(column) != pivot) {
                return r;
            }
        }

        return rowLimit;
    }

    private static int nextPivotColumn(String[] table, int column, int row, int rowLimit) {
        String cells = table[row];
        int columnLimit = cells.length();

        for (int c = column + 1; c < columnLimit; ++c) {
            if (nextPivotRow(table, cells.charAt(c), c, row, rowLimit) < rowLimit) {
                return c;
            }
        }

        return columnLimit;
    }

    private static final class Overflow implements ToIntFunction<String> {

        private final ImmutableStringTrie trie;
        private final String overflowKey;
        private final ToIntFunction<String> next;

        Overflow(char[] data, String overflowKey, ToIntFunction<String> next) {
            super();
            this.trie = new ImmutableStringTrie(data);
            this.overflowKey = overflowKey;
            this.next = next;
        }

        public int applyAsInt(String key) {
            return key.compareTo(this.overflowKey) < 0 ? this.trie.applyAsInt(key) : 16384 + this.next.applyAsInt(key);
        }
    }
}
