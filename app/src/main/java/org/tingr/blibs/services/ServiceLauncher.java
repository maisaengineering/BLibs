package org.tingr.blibs.services;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServiceLauncher extends Fragment {
    private static final String NAMESPACE = "namespace";
    private static final String TYPE = "type";

    private String namespace;
    private String type;

    public ServiceLauncher() {
    }

    public static ServiceLauncher newInstance(String namespace, String type) {
        ServiceLauncher fragment = new ServiceLauncher();
        Bundle args = new Bundle();
        args.putString(NAMESPACE, namespace);
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            namespace = getArguments().getString(NAMESPACE);
            type = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
