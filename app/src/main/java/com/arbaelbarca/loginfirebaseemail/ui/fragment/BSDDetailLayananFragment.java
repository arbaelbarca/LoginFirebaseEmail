package com.arbaelbarca.loginfirebaseemail.ui.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BSDDetailLayananFragment extends BottomSheetDialogFragment {

    ModelJasaLayanan modelJasaLayanan;
    @BindView(R.id.imgTukang)
    CircleImageView imgTukang;
    @BindView(R.id.edAddNoteds)
    EditText edAddNoteds;
    Unbinder unbinder;
    BottomSheetBehavior behavior;

    public BSDDetailLayananFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                behavior = BottomSheetBehavior.from(bottomSheet);

            }

            behavior.setPeekHeight(300);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    switch (i) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            Log.d("responClose", " hidden ");
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            Log.d("responClose", " sheet ");
                        }
                        break;
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            Log.d("responClose", " collaps ");

                        }
                        break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            Log.d("responClose", " expand ");
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            Log.d("responClose", " settling ");
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });

            if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        return bottomSheetDialog;
    }

    public static BSDDetailLayananFragment newInstance(ModelJasaLayanan jasaLayanan) {
        BSDDetailLayananFragment bsdDetailLayananFragment = new BSDDetailLayananFragment();
        bsdDetailLayananFragment.modelJasaLayanan = jasaLayanan;
        return bsdDetailLayananFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_buttom_sheet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        bottomSheetBehavior = BottomSheetBehavior.from(frameBottomSheet);

        init();
    }

    private void init() {
        if (modelJasaLayanan != null) {
            edAddNoteds.setText(modelJasaLayanan.getTextTambahan());

        }

//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//


    }

}
