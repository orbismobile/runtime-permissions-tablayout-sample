package pe.com.orbis.runtimepermissions_sample.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import pe.com.orbis.runtimepermissions_sample.R;
import pe.com.orbis.runtimepermissions_sample.util.Constant;
import pe.com.orbis.runtimepermissions_sample.util.FileSystemUtils;
import pe.com.orbis.runtimepermissions_sample.util.RuntimePermissionUtils;
import pe.com.orbis.runtimepermissions_sample.view.adapter.GalleryAdapter;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by carlos on 22/06/16.
 * Alias: CarlitosDroid
 */
public class GalleryFragment extends Fragment implements View.OnClickListener {

    private static final String[] PERMS_ALL = {
            WRITE_EXTERNAL_STORAGE
    };

    private static final String STORAGE_PERMISSION_DIALOG_OPEN_KEY = "already_open_dialog_permission";

    private RecyclerView rcvGallery;
    private StaggeredGridLayoutManager linearLayoutManager;
    private GalleryAdapter galleryAdapter;

    private LinearLayout lnlNotPermission;
    private AppCompatButton btnActivatePermission;

    private List<String> listFilePaths = new ArrayList<>();

    // true if dialog already open
    private boolean alreadyAskedForStoragePermission = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            alreadyAskedForStoragePermission = savedInstanceState.getBoolean(STORAGE_PERMISSION_DIALOG_OPEN_KEY, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STORAGE_PERMISSION_DIALOG_OPEN_KEY, alreadyAskedForStoragePermission);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        btnActivatePermission = (AppCompatButton) view.findViewById(R.id.btnActivatePermission);
        lnlNotPermission = (LinearLayout) view.findViewById(R.id.lnlNotPermission);
        rcvGallery = (RecyclerView) view.findViewById(R.id.rcvGallery);

        linearLayoutManager = new StaggeredGridLayoutManager(4, GridLayoutManager.VERTICAL);
        rcvGallery.setLayoutManager(linearLayoutManager);
        galleryAdapter = new GalleryAdapter(this, listFilePaths);
        rcvGallery.setAdapter(galleryAdapter);

        if (!RuntimePermissionUtils.hasPermission(getActivity() ,WRITE_EXTERNAL_STORAGE) && RuntimePermissionUtils.useRuntimePermissions()) {
            if(!alreadyAskedForStoragePermission){
                // don't check again because the dialog is still open
                alreadyAskedForStoragePermission = true;
                requestPermissions(PERMS_ALL, Constant.RESULT_PERMS_ALL);
            }
        } else {
            showGallery();
        }
        btnActivatePermission.setOnClickListener(this);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.RESULT_PERMS_ALL) {
            // the request returned a result so the dialog is closed
            alreadyAskedForStoragePermission = false;
            if (RuntimePermissionUtils.hasPermission(getActivity() ,WRITE_EXTERNAL_STORAGE)) {
                showGallery();
            }else{
                lnlNotPermission.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showGallery(){
        if(lnlNotPermission.getVisibility()==View.VISIBLE){
            lnlNotPermission.setVisibility(View.GONE);
        }
        listFilePaths.clear();
        listFilePaths.addAll(FileSystemUtils.getAllImage(getActivity()));
        galleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (RuntimePermissionUtils.hasPermission(getActivity() ,WRITE_EXTERNAL_STORAGE)) {
            showGallery();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnActivatePermission:
                boolean showRationale = shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                if(!showRationale){//User check "NEVER ASK AGAIN" in permission dialog
                    DetailsSettingsDialogFragment detailsSettingsDialogFragment = DetailsSettingsDialogFragment.newInstance(
                            getActivity().getString(R.string.profile_storage_permission));
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(detailsSettingsDialogFragment, "layout_filter_checkbox_dialog");
                    detailsSettingsDialogFragment.setTargetFragment(this, 123);//communication between fragment and dialgofragment
                    fragmentTransaction.commit();
                }else{
                    if (!RuntimePermissionUtils.hasPermission(getActivity(), WRITE_EXTERNAL_STORAGE)
                            && RuntimePermissionUtils.useRuntimePermissions()) {
                        requestPermissions(PERMS_ALL, Constant.RESULT_PERMS_ALL);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_PERMISSION_SETTING){
            if (RuntimePermissionUtils.hasPermission(getActivity() ,WRITE_EXTERNAL_STORAGE)) {
                showGallery();
            }
        }
    }
}
