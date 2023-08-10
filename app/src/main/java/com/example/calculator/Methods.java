package com.example.calculator;

import android.widget.EditText;

import java.util.Locale;

public class Methods {
    public static String getOperator(String operation) {
        String operator = operation.contains(Consts.OPERATOR_MULT) ? Consts.OPERATOR_MULT :
                operation.contains(Consts.OPERATOR_SPLIT) ? Consts.OPERATOR_SPLIT :
                        operation.contains(Consts.OPERATOR_ADD) ? "\\" + Consts.OPERATOR_ADD : Consts.OPERATOR_NULL;

        if (operator.equals(Consts.OPERATOR_NULL) && operation.lastIndexOf(Consts.OPERATOR_SUB) > 0) {
            operator = Consts.OPERATOR_SUB;
        }
        return operator;
    }

    public static void tryResolve(boolean fromResult, EditText etInput, OnResolveCallback callback) {
        String operation = etInput.getText().toString();

        if (operation.isEmpty()) {
            return;
        }

        if (operation.contains(Consts.DOT) &&
                operation.lastIndexOf(Consts.DOT) == operation.length() - 1) {
            operation = operation.substring(0, operation.length() - 1);
        }

        String operator = Methods.getOperator(operation);
        String[] values = new String[0];
        if (!operator.equals(Consts.OPERATOR_NULL)) {
            if (operator.equals(Consts.OPERATOR_SUB)) {
                final int index = operation.lastIndexOf(Consts.OPERATOR_SUB);
                values = new String[2];
                values[0] = operation.substring(0, index);
                values[1] = operation.substring(index + 1);
            } else {
                values = operation.split(operator);
            }
        }

        if (values.length > 1) {
            try {
                final double numberOne = Double.valueOf(values[0]);
                final double numberTwo = Double.valueOf(values[1]);
                etInput.getText().clear();
                callback.onIsEditing();
                etInput.append(String.format(Locale.ROOT, "%.2f",
                        Methods.getResult(numberOne, operator, numberTwo)));
            } catch (NumberFormatException e) {
                if (fromResult) {
                    callback.onShowMessage(R.string.message_exp_incorrect);
                }
            }
        } else {
            if (fromResult) {
                if (!operator.equals(Consts.OPERATOR_NULL)){
                    callback.onShowMessage(R.string.message_exp_incorrect);
                }
            }
        }
    }

    public static double getResult(double numberOne, String operator, double numberTwo) {
        double result = 0;

        operator = operator.replace("\\", "");

        switch (operator) {
            case Consts.OPERATOR_MULT:
                result = numberOne * numberTwo;
                break;
            case Consts.OPERATOR_SPLIT:
                result = numberOne / numberTwo;
                break;
            case Consts.OPERATOR_ADD:
                result = numberOne + numberTwo;
                break;
            case Consts.OPERATOR_SUB:
                result = numberOne - numberTwo;
                break;
        }

        return result;
    }

    public static boolean canReplaceOperator(CharSequence s) {
        if (s.length() < 2) {
            return false;
        }
        final String ultimoCaracter = String.valueOf(s.charAt(s.length()-1));
        final String penultimoCaracter = String.valueOf(s.charAt(s.length()-2));

        return (ultimoCaracter.equals(Consts.OPERATOR_MULT) ||
                ultimoCaracter.equals(Consts.OPERATOR_SPLIT) ||
                ultimoCaracter.equals(Consts.OPERATOR_ADD)) &&
                (String.valueOf(s.charAt(s.length() - 2)).equals(Consts.OPERATOR_MULT) ||
                penultimoCaracter.equals(Consts.OPERATOR_SPLIT) ||
                penultimoCaracter.equals(Consts.OPERATOR_ADD) ||
                penultimoCaracter.equals(Consts.OPERATOR_SUB));
    }
}
