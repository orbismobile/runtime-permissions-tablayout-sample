package pe.com.orbis.runtimepermissions_sample.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import pe.com.orbis.runtimepermissions_sample.R;


/**
 * Created by Carlos Vargas on 13/05/16.
 * Alias: CarlitosDroid
 */
public class DetailsSettingsDialogFragment extends DialogFragment implements View.OnClickListener{

    private AppCompatButton btnSendEmail;
    private AppCompatTextView lblDescription;
    private static final int REQUEST_PERMISSION_SETTING = 1338;
    private String description;

    public DetailsSettingsDialogFragment(){

    }

    public static DetailsSettingsDialogFragment newInstance(String description){
        DetailsSettingsDialogFragment frag = new DetailsSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("description", description);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        description = getArguments().getString("description");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_detail_setting, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        btnSendEmail = (AppCompatButton) view.findViewById(R.id.btnSendEmail);
        lblDescription = (AppCompatTextView) view.findViewById(R.id.lblDescription);
        lblDescription.setText(description);
        btnSendEmail.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendEmail:
                getDialog().dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                getTargetFragment().startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                break;
            default:
                break;
        }
    }
}
