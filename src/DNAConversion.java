public class DNAConversion {
    private static final byte A = 0;
    private static final byte T = 3;
    private static final byte C = 1;
    private static final byte G = 2;

    public static long dnaToLong(String sequence) {
        long l = 0;
        char[] chars = sequence.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (Character.toLowerCase(chars[i])) {
                case 'a':
                    l += A;
                    break;
                case 't':
                    l += T;
                    break;
                case 'c':
                    l += C;
                    break;
                case 'g':
                    l += G;
            }
            if (i < chars.length - 1) l <<= 2;
        }
        return l;
    }

    public static String longToDna(long l, int sequenceLength) {
        char[] chars = new char[sequenceLength];
        for (int i = sequenceLength - 1; i >= 0; i--) {
            switch ((byte) (l % 4)) {
                case A:
                    chars[i] = 'a';
                    break;
                case T:
                    chars[i] = 't';
                    break;
                case C:
                    chars[i] = 'c';
                    break;
                case G:
                    chars[i] = 'g';
            }
            l >>= 2;
        }
        return String.valueOf(chars);
    }
}
