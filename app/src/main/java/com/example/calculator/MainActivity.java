package com.example.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.calculator.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Boolean isEditingProgress = false;
    private int minLenght;
    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        minLenght = getResources().getInteger(R.integer.main_min_length);
        textSize = getResources().getInteger(R.integer.main_input_textSize);


        onViewClicked();
        configEditText();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configEditText() {

        // binding.etInput.setInputType(InputType.TYPE_NULL);

        binding.etInput.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >=
                            (binding.etInput.getRight() -
                                    binding.etInput.getCompoundDrawables()[Consts.DRAWABLE_RIGHT]
                                            .getBounds().width())) {

                        if(binding.etInput.length() > 0) {
                            int lenght = binding.etInput.getText().length();
                            binding.etInput.getText().delete(lenght - 1, lenght);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        binding.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isEditingProgress && Methods.canReplaceOperator(charSequence)) {
                    isEditingProgress = true;
                    int lenght = binding.etInput.getText().length();
                    binding.etInput.getText().delete(lenght - 2, lenght - 1);
                }
                if(charSequence.length() > minLenght) {
                    int modifiedTextSize = textSize - (((charSequence.length() - minLenght) * 2) +
                            (charSequence.length() - minLenght));

                    int newTextSize = modifiedTextSize <= 17 ? 20 : modifiedTextSize;

                    binding.etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSize);
                } else {
                    binding.etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { isEditingProgress = false; }
        });
    }

    public void onViewClicked() {

        View[] buttons = new View[] {binding.btnOne, binding.btnTwo, binding.btnThree,
                binding.btnFour, binding.btnFive, binding.btnSix, binding.btnSeven,
                binding.btnEight, binding.btnNine, binding.btnZero};

        for(View button : buttons) {

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.etInput.append(((MaterialButton)view).getText().toString());
                }
            });
        }

        View[] buttonsOperators = new View[] {binding.btnAdd, binding.btnSub, binding.btnSplit,
                binding.btnMult};

        for(View button : buttonsOperators) {

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resolve(false);
                    String operator = ((MaterialButton)view).getText().toString();
                    String operation = binding.etInput.getText().toString();
                    String lastCharacter =
                            operation.isEmpty() ? "" :
                                    operation.substring(operation.length() - 1);

                    if(operator.equals(Consts.OPERATOR_SUB)) {
                        if(operation.isEmpty() ||
                                (!(lastCharacter.equals(Consts.OPERATOR_SUB)) &&
                                        !(lastCharacter.equals(Consts.DOT)))) {
                            binding.etInput.append(operator);
                        }
                    } else {
                        if(!operation.isEmpty() &&
                                !(lastCharacter.equals(Consts.OPERATOR_SUB)) &&
                                !(lastCharacter.equals(Consts.DOT))) {
                            binding.etInput.append(operator);
                        }
                    }
                }
            });
        }

        binding.btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operation = binding.etInput.getText().toString();
                String operator = Methods.getOperator(operation);
                int count = operation.length() -
                        operation.replace(".", "").length();

                if(!operation.contains(Consts.DOT) || count < 2 &&
                        (!operator.equals(Consts.OPERATOR_NULL))) {

                    binding.etInput.append(binding.btnDot.getText());
                }
            }
        });

        binding.btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etInput.setText("");
            }
        });

        binding.btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolve(true);
            }
        });
    }

    private void resolve(boolean fromResult) {
        Methods.tryResolve(fromResult, binding.etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                isEditingProgress = true;
            }
        });
    }

    private void showMessage(int errorRes) {
        Snackbar.make(binding.rlContainer, errorRes, Snackbar.LENGTH_SHORT).show();
    }
}