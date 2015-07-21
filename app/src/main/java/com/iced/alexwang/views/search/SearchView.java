package com.iced.alexwang.views.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.views.search_result.SearchResultView;

public class SearchView extends RelativeLayout {
    public SearchView(Context context) {
        super(context);
        initViews();
    }
    public SearchView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    void initViews() {
        RelativeLayout main = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_search, this);
        final SearchResultView resultView = (SearchResultView) main.findViewById(R.id.resultViewSearch);
        Button btnSearch = (Button) main.findViewById(R.id.btnSearchSearch);
        EditText content = (EditText) main.findViewById(R.id.editTextSearchContent);

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 3) {
                    // do nothing
                }
                else if (s.length() == 3) {
                    resultView.searchAll(s.toString());
                    resultView.flush();
                } else if (s.length() > last) {
                    resultView.searchAllFromResult(s.toString());
                    resultView.flush();
                } else if (s.length() < last) {
                    resultView.searchAll(s.toString());
                    resultView.flush();
                }
                last = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            int last = 0;
        });
    }
}
