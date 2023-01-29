import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * В теле класса решения разрешено использовать только финальные переменные типа RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author :TODO: LastName FirstName
 */
public class Solution implements MonotonicClock {
    private final RegularInt c1_1 = new RegularInt(0);
    private final RegularInt c1_2 = new RegularInt(0);
    private final RegularInt c1_3 = new RegularInt(0);
    private final RegularInt c2_1 = new RegularInt(0);
    private final RegularInt c2_2 = new RegularInt(0);
    private final RegularInt c2_3 = new RegularInt(0);

    @Override
    public void write(@NotNull Time time) {
        c2_1.setValue(time.getD1());
        c2_2.setValue(time.getD2());
        c2_3.setValue(time.getD3());
        c1_3.setValue(time.getD3());
        c1_2.setValue(time.getD2());
        c1_1.setValue(time.getD1());
    }

    @NotNull
    @Override
    public Time read() {
        int[] r1 = new int[3];
        int[] r2 = new int[3];
        r1[0] = c1_1.getValue();
        r1[1] = c1_2.getValue();
        r1[2] = c1_3.getValue();
        r2[2] = c2_3.getValue();
        r2[1] = c2_2.getValue();
        r2[0] = c2_1.getValue();
        if (Arrays.equals(r1, r2)) {
            return new Time(r1[0], r1[1], r1[2]);
        }
        int idx = 0;
        while (idx < 3 && r1[idx] == r2[idx]) {
            idx++;
        }
        int p = idx - 1;
        int[] r3 = new int[3];
        System.arraycopy(r1, 0, r3, 0, p + 1);
        if (p + 1 < 3) {
            r3[p + 1] = r2[p + 1];
        }
        for (int i = p + 2; i < 3; i++) {
            r3[i] = 0;
        }
        return new Time(r3[0], r3[1], r3[2]);
    }
}
