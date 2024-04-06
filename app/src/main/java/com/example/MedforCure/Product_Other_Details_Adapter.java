package com.example.MedforCure;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Product_Other_Details_Adapter extends RecyclerView.Adapter<Product_Other_Details_Adapter.ViewHolder> {

    private List<Product_Other_Details_Model> product_other_details_modelList;

    public Product_Other_Details_Adapter(List<Product_Other_Details_Model> product_other_details_modelList) {
        this.product_other_details_modelList = product_other_details_modelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (product_other_details_modelList.get(position).getType()) {
            case 0:
                return Product_Other_Details_Model.OTHER_DETAILS_TITLE;
            case 1:
                return Product_Other_Details_Model.OTHER_DETAILS_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public Product_Other_Details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case Product_Other_Details_Model.OTHER_DETAILS_TITLE:
                TextView title = new TextView(viewGroup.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(setDp(16, viewGroup.getContext()), setDp(16, viewGroup.getContext()), setDp(16, viewGroup.getContext()), setDp(8, viewGroup.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);
            case Product_Other_Details_Model.OTHER_DETAILS_BODY:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_other_details_item_layout, viewGroup, false);
                return new ViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull Product_Other_Details_Adapter.ViewHolder viewHolder, int position) {
        switch(product_other_details_modelList.get(position).getType())
        {
            case Product_Other_Details_Model.OTHER_DETAILS_TITLE:
                viewHolder.setTitle(product_other_details_modelList.get(position).getTitle());
                break;
            case Product_Other_Details_Model.OTHER_DETAILS_BODY:
                String argumentTitle = product_other_details_modelList.get(position).getArgumentName();
                String argumentDetail = product_other_details_modelList.get(position).getArgumentValue();
                viewHolder.setArguments(argumentTitle, argumentDetail);
                break;

            default:return;
        }



    }

    @Override
     public int getItemCount() {
        return product_other_details_modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView argumentname;
        private TextView argumentvalue;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        private void setTitle(String titleText)
        {
            title = (TextView) itemView;
            title.setText(titleText);
        }

        private void setArguments(String argumentTitle, String argumentdetail) {
            argumentname = itemView.findViewById(R.id.tv_argument_name);
            argumentvalue = itemView.findViewById(R.id.tv_argument_value);
            argumentname.setText(argumentTitle);
            argumentvalue.setText(argumentdetail);

        }
    }

    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
