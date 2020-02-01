package com.scanner.rmcode.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;

public class ResultFragment extends Fragment implements BaseFragment {

    private Context mContext;
    private Activity mFragmentActivity;

    View resultFragmentView;

    Drawable background;
    Drawable buttonDrawable;
    ColorStateList accentColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_fragment, container, false);
        resultFragmentView = view;
        setTheme();
        setFonts(view);

        final String result = ((MainActivity) mFragmentActivity).getResult();
        if (result != null && !result.isEmpty() && !result.contains("ERROR")) {
            LinearLayout resultPlaceholder = view.findViewById(R.id.result_placeholder);
            resultPlaceholder.setVisibility(View.VISIBLE);

            TextView resultText = view.findViewById(R.id.result_text);
            resultText.setText(result);

            LinearLayout positiveResultButtons = view.findViewById(R.id.positive_result_buttons);
            positiveResultButtons.setVisibility(View.VISIBLE);

            ImageButton copyBtn = view.findViewById(R.id.copy_button);
            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyResultToClipboard(result);
                }
            });

            final ImageButton shareBtn = view.findViewById(R.id.share_button);
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareResult(result);
                }
            });

            ImageButton browserBtn = view.findViewById(R.id.open_in_browser_button);
            browserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mFragmentActivity).navigateToBrowser(result);
                }
            });

        } else {
            LinearLayout noResultPlaceholder = view.findViewById(R.id.no_result_placeholder);
            noResultPlaceholder.setVisibility(View.VISIBLE);

            Button tryAgainBtn = view.findViewById(R.id.try_again_result_button);
            tryAgainBtn.setVisibility(View.VISIBLE);
            tryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.code_scanner));
                }
            });

        }

        return view;
    }

    private void setFonts(View view) {
        Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/Kalam-Regular.ttf");

        TextView resultTitle = view.findViewById(R.id.result_title);
        resultTitle.setTypeface(type);

        TextView resultText = view.findViewById(R.id.result_text);
        resultText.setTypeface(type);

        TextView noResultTitle = view.findViewById(R.id.no_result_title);
        noResultTitle.setTypeface(type);

        TextView noResultMessage = view.findViewById(R.id.no_result_message);
        noResultMessage.setTypeface(type);

        Button tryAgainButton = view.findViewById(R.id.try_again_result_button);
        tryAgainButton.setTypeface(type);
    }

    private void copyResultToClipboard(String result) {
        ClipboardManager clipboard = (ClipboardManager) mFragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("codeScannerResult", result);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), mContext.getResources().getString(R.string.copy_toast_message), Toast.LENGTH_LONG).show();
    }

    private void shareResult(String result) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, result);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setThemeDetails(Drawable back, Drawable buttonBack, ColorStateList accent) {
        background = back;
        buttonDrawable = buttonBack;
        accentColor = accent;

        if (resultFragmentView != null) {
            setTheme();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTheme() {
        resultFragmentView.setBackground(background);

        Button tryAgainButton = resultFragmentView.findViewById(R.id.try_again_result_button);
        tryAgainButton.setBackground(buttonDrawable);

        ImageButton copyButton = resultFragmentView.findViewById(R.id.copy_button);
        copyButton.setBackground(buttonDrawable);
        copyButton.setImageTintList(accentColor);

        ImageButton shareButton = resultFragmentView.findViewById(R.id.share_button);
        shareButton.setBackground(buttonDrawable);
        shareButton.setImageTintList(accentColor);

        ImageButton navigateButton = resultFragmentView.findViewById(R.id.open_in_browser_button);
        navigateButton.setBackground(buttonDrawable);
        navigateButton.setImageTintList(accentColor);
    }
}
