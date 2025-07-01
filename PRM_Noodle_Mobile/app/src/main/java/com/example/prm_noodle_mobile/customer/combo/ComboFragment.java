package com.example.prm_noodle_mobile.customer.combo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Combo;
import com.example.prm_noodle_mobile.customer.combodetail.ComboDetailFragment;
import java.util.ArrayList;
import java.util.List;

public class ComboFragment extends Fragment implements ComboContract.View {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ComboPresenter presenter;
    private ComboListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_combo, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_combos);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComboListAdapter(new ArrayList<>());
        adapter.setOnComboClickListener(combo -> {
            Bundle args = new Bundle();
            args.putInt("comboId", combo.getComboId());
            ComboDetailFragment detailFragment = new ComboDetailFragment();
            detailFragment.setArguments(args);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        recyclerView.setAdapter(adapter);
        presenter = new ComboPresenter(this);
        presenter.loadCombos();
        return view;
    }

    @Override
    public void showCombos(List<Combo> combos) {
        adapter.setCombos(combos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onDestroy();
    }
} 