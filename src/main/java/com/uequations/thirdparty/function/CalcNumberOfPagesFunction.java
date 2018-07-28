package com.uequations.thirdparty.function;

import java.util.function.ToIntBiFunction;

/**
 * @author Mensah Alkebu-lan <malkebu-lan@uequations.com>
 */
public class CalcNumberOfPagesFunction implements ToIntBiFunction<Integer, Integer> {

    @Override
    public int applyAsInt(Integer dividend, Integer divisor) {

        Integer mod = Math.floorMod(dividend, divisor);
        Integer quotient = Math.floorDiv(dividend, divisor);

        return (mod > 0) ? quotient + 1 : quotient;
    }
}
