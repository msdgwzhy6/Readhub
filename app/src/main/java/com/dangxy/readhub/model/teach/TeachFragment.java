package com.dangxy.readhub.model.teach;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.dangxy.readhub.R;
import com.dangxy.readhub.ReadhubApplication;
import com.dangxy.readhub.base.BaseLazyFragment;
import com.dangxy.readhub.entity.TeachEntity;
import com.dangxy.readhub.model.DetailActivity;
import com.dangxy.readhub.room.Wait;
import com.dangxy.readhub.room.WaitDataBase;

import butterknife.BindView;

/**
 * @description  描述
 * @author  dangxy99
 * @date   2017/12/30
 */
public class TeachFragment extends BaseLazyFragment implements TeachContract.ITeachView, TeachListAdapter.DetailClickListener {


    @BindView(R.id.rv_teach)
    RecyclerView rvTeach;
    @BindView(R.id.srl_teach)
    SwipeRefreshLayout srlTeach;
    private TeachPresenter teachPresenter;
    private TeachListAdapter teachListAdapter;
    private Wait wait;

    public TeachFragment() {
    }


    @Override
    protected void loadData() {
        teachPresenter = new TeachPresenter(this);
        teachPresenter.getData();
        teachPresenter.setRefresh(srlTeach, rvTeach);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_teach;
    }

    @Override
    protected void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReadhubApplication.getInstance());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTeach.setLayoutManager(linearLayoutManager);
    }


    @Override
    public void setRefresh(TeachEntity teachEntity) {
        teachListAdapter.refresh(teachEntity.getData());
    }

    @Override
    public void getTeachEntity(TeachEntity teachEntity, boolean isFirst) {
        if(isFirst){
            teachListAdapter = new TeachListAdapter(teachEntity.getData());
            rvTeach.setAdapter(teachListAdapter);
        }else {
            teachListAdapter.addAll(teachEntity.getData());
        }

        teachListAdapter.setOnDetailClickListener(this);
    }

    @Override
    public void onDetailClickListener(String title, String summary, String url) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("summary", summary);
        startActivity(intent);
    }
    @Override
    public void onWaitClickListener(final ImageView imageView, String id, String title, String summary, String url) {
        wait = new Wait(id, title, summary, url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                WaitDataBase.getDatabase().waitDao().addWait(wait);
            }
        }).start();
        Snackbar.make(imageView, "已添加到稍后再看", Snackbar.LENGTH_SHORT).setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();

    }

}
