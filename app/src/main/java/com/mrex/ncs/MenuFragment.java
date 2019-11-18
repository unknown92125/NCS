package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    ArrayList<MenuList> arrListMenu = new ArrayList<>();
    RecyclerView recyclerView;
    MenuRecyclerAdapter menuRecyclerAdapter;
    MenuList menuList;

    public MenuFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        arrListMenu.add(new MenuList(R.drawable.ic_volume_up, "공지사항"));
        arrListMenu.add(new MenuList(R.drawable.ic_phone_in_talk, "상담"));
        arrListMenu.add(new MenuList(R.drawable.ic_person, "직원"));

        recyclerView = view.findViewById(R.id.rv);
        menuRecyclerAdapter = new MenuRecyclerAdapter();
        recyclerView.setAdapter(menuRecyclerAdapter);

        return view;
    }

    public class MenuRecyclerAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.menu_recycler_view, parent, false);

            VHolder vHolder = new VHolder(itemView);

            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            VHolder vHolder = (VHolder) holder;
            menuList = arrListMenu.get(position);

            vHolder.iv.setImageResource(menuList.getImage());
            vHolder.tv.setText(menuList.getMenuText());
        }

        @Override
        public int getItemCount() {
            return arrListMenu.size();
        }

        public class VHolder extends RecyclerView.ViewHolder {

            private ImageView iv;
            private TextView tv;

            public VHolder(@NonNull View itemView) {
                super(itemView);

                iv = itemView.findViewById(R.id.iv_icon);
                tv = itemView.findViewById(R.id.tv_menu);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getLayoutPosition();
                        if (position == 2) {
                            startActivity(new Intent(getContext(), ManagerActivity.class));
//                        intent=new Intent(context, HomeFirstActivity.class);
//                        context.startActivity(intent);
                        }

                    }
                });
            }
        }
    }

    public class MenuList {

        int image;
        String menuText;

        public MenuList(int image, String menuText) {
            this.image = image;
            this.menuText = menuText;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getMenuText() {
            return menuText;
        }

        public void setMenuText(String menuText) {
            this.menuText = menuText;
        }
    }

}
