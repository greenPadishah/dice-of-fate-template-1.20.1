package net.bozkurt.dof.wager;

public enum WagerType {
    IRON,
    DIAMOND,
    LIFE;

    public static WagerType fromOrdinal(int ordinal) {
        WagerType[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return IRON;
        }
        return values[ordinal];
    }
}
