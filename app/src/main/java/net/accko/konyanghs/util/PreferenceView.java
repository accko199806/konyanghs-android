package net.accko.konyanghs.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.accko.konyanghs.R;

public class PreferenceView extends LinearLayout {
    ImageView symbol;
    TextView name;
    TextView text;

    public PreferenceView(Context context) {
        super(context);
        initView();
    }

    public PreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public PreferenceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.item_info_preference, this, false);
        addView(v);
        symbol = findViewById(R.id.symbol);
        name = findViewById(R.id.name);
        text = findViewById(R.id.text);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PreferenceView);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PreferenceView, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        int symbolResID = typedArray.getResourceId(R.styleable.PreferenceView_symbol, 0);

        String nameString = typedArray.getString(R.styleable.PreferenceView_name);
        name.setText(nameString);

        String textString = typedArray.getString(R.styleable.PreferenceView_text);

        if (symbolResID == 0) { //이미지가 없을 때 예외 처리
            symbol.setVisibility(View.GONE);
            name.setGravity(Gravity.RIGHT);
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        } else {
            symbol.setImageResource(symbolResID);
        }

        if (textString.isEmpty()) { //텍스트가 없을 때 예외처리
            text.setVisibility(View.GONE);
        } else {
            text.setText(textString);
        }
        typedArray.recycle();
    }

    void setSymbol(int symbol_resID) {
        symbol.setImageResource(symbol_resID);
    }

    void setName(String name_string) {
        name.setText(name_string);
    }

    void setName(int name_resID) {
        name.setText(name_resID);
    }

    void setText(String text_string) {
        text.setText(text_string);
    }

    void setText(int text_resID) {
        text.setText(text_resID);
    }

}