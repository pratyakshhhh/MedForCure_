package com.example.MedforCure;

import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Horizontal_Product_Scroll_Adapter extends RecyclerView.Adapter<Horizontal_Product_Scroll_Adapter.ViewHolder> {
    private List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList;

    public Horizontal_Product_Scroll_Adapter(List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public Horizontal_Product_Scroll_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Horizontal_Product_Scroll_Adapter.ViewHolder viewholder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProduct_image();
        String title = horizontalProductScrollModelList.get(position).getProduct_title();
        String brand = horizontalProductScrollModelList.get(position).getProduct_brand();
        String price = horizontalProductScrollModelList.get(position).getProduct_price();
        String productId = horizontalProductScrollModelList.get(position).getProductID();

       viewholder.setData(productId,resource,title,brand,price);

    }

    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelList.size() > 8) {
            return 8;
        } else {
            return horizontalProductScrollModelList.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView productBrand;
        private TextView productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productBrand = itemView.findViewById(R.id.h_s_product_brand);
            productPrice = itemView.findViewById(R.id.h_s_product_price);

        }

        private void setData(final String productId, String resource, String title, String brand, String price) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_categories)).into(productImage);
            productTitle.setText(title);
            productBrand.setText(brand);
            productPrice.setText("Rs. " + price + "/-");
            if (!title.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID",productId);
                        itemView.getContext().startActivity(productDetailsIntent);

                    }
                });
            }
        }

    }
}
