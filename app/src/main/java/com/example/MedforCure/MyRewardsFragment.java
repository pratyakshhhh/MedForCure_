package com.example.MedforCure;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyRewardsFragment extends Fragment {


    public MyRewardsFragment() {

    }

    private RecyclerView rewardsRecyclerView;
    private Dialog loadingDialog;
    public static RewardAdapter rewardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false); // Agar user dialog ke bahar click karega toh dialog khud bandd ho jayega
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        rewardsRecyclerView = view.findViewById(R.id.my_rewards_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);
        rewardAdapter = new RewardAdapter(DBqueries.rewardModelList, false);
        rewardsRecyclerView.setAdapter(rewardAdapter);
        if (DBqueries.rewardModelList.size() == 0) {
            DBqueries.loadRewards(getContext(), loadingDialog, true);
        } else {
            loadingDialog.dismiss();
        }

        rewardAdapter.notifyDataSetChanged();
        return view;
    }
}