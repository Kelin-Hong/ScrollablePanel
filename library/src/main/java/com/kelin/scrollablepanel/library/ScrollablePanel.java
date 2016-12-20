package com.kelin.scrollablepanel.library;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;

/**
 * A flexible view for providing a limited window into a large data set,like a two-dimensional recyclerView.
 * but it will pin the itemView of first row and first column in their original location.
 */
public class ScrollablePanel extends FrameLayout {
    protected RecyclerView recyclerView;
    protected PinFirstItemRecyclerView headerRecyclerView;
    protected PanelLineAdapter panelLineAdapter;
    protected PanelAdapter panelAdapter;

    public ScrollablePanel(Context context, PanelAdapter panelAdapter) {
        super(context);
        this.panelAdapter = panelAdapter;
        initView();
    }

    public ScrollablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScrollablePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_scrollable_panel, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_content_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        headerRecyclerView = (PinFirstItemRecyclerView) findViewById(R.id.recycler_header_list);
        headerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (panelAdapter != null) {
            panelLineAdapter = new PanelLineAdapter(panelAdapter, headerRecyclerView);
            recyclerView.setAdapter(panelLineAdapter);
        }
    }

    /**
     * @param panelAdapter {@link PanelAdapter}
     */
    public void setPanelAdapter(PanelAdapter panelAdapter) {
        if (this.panelLineAdapter != null) {
            panelLineAdapter.setPanelAdapter(panelAdapter);
            panelLineAdapter.notifyDataSetChanged();
        } else {
            panelLineAdapter = new PanelLineAdapter(panelAdapter, headerRecyclerView);
            recyclerView.setAdapter(panelLineAdapter);
        }
        this.panelAdapter = panelAdapter;

    }

    /**
     * Adapter used to bind dataSet to cell View that are displayed within every row of {@link ScrollablePanel}.
     */
    private static class PanelLineItemAdapter extends RecyclerView.Adapter {

        private PanelAdapter panelAdapter;
        private int row;

        public PanelLineItemAdapter(int row, PanelAdapter panelAdapter) {
            this.row = row;
            this.panelAdapter = panelAdapter;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return this.panelAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            this.panelAdapter.onBindViewHolder(holder, row, position);
        }

        @Override
        public int getItemViewType(int position) {
            return this.panelAdapter.getItemViewType(row, position);
        }


        @Override
        public int getItemCount() {
            return panelAdapter.getColumnCount() + 1;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }


    /**
     * Adapter used to bind dataSet to views that are displayed within a{@link ScrollablePanel}.
     */
    private static class PanelLineAdapter extends RecyclerView.Adapter<PanelLineAdapter.ViewHolder> {

        private PanelAdapter panelAdapter;
        private PinFirstItemRecyclerView headerRecyclerView;
        private HashSet<RecyclerView> observerList = new HashSet<>();


        public PanelLineAdapter(PanelAdapter panelAdapter, PinFirstItemRecyclerView headerRecyclerView) {
            this.panelAdapter = panelAdapter;
            this.headerRecyclerView = headerRecyclerView;
            initRecyclerView(headerRecyclerView);
            setUpHeaderRecyclerView();

        }

        public void setPanelAdapter(PanelAdapter panelAdapter) {
            this.panelAdapter = panelAdapter;
            setUpHeaderRecyclerView();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return panelAdapter.getRowCount();
        }

        @Override
        public PanelLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PanelLineAdapter.ViewHolder viewHolder = new PanelLineAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_content_row, parent, false));
            initRecyclerView(viewHolder.recyclerView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PanelLineItemAdapter lineItemAdapter = (PanelLineItemAdapter) holder.recyclerView.getAdapter();
            if (lineItemAdapter == null) {
                lineItemAdapter = new PanelLineItemAdapter(position + 1, panelAdapter);
                holder.recyclerView.setAdapter(lineItemAdapter);
            } else {
                lineItemAdapter.setRow(position + 1);
                lineItemAdapter.notifyDataSetChanged();
            }

        }


        private void setUpHeaderRecyclerView() {
            if (panelAdapter != null) {
                PanelLineItemAdapter lineItemAdapter = new PanelLineItemAdapter(0, panelAdapter);
                headerRecyclerView.setAdapter(lineItemAdapter);
            }
        }

        public void initRecyclerView(RecyclerView recyclerView) {
            observerList.add(recyclerView);
            recyclerView.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent)
				{
					switch (motionEvent.getAction())
					{
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_POINTER_DOWN:
							for (RecyclerView rv : observerList)
							{
								rv.stopScroll();
							}
					}
					
					return false;
				}
			});
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                int state;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                    View firstVisibleItem = linearLayoutManager.getChildAt(0);
                    if (firstVisibleItem != null) {
                        int firstRight = linearLayoutManager.getDecoratedRight(firstVisibleItem);
                        for (RecyclerView rv : observerList) {
                            if (recyclerView != rv) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.scrollToPositionWithOffset(firstPos + 1, firstRight);
                                }
                            }

                        }
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    state = newState;
                }
            });
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public RecyclerView recyclerView;

            public ViewHolder(View view) {
                super(view);
                this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_line_list);
                this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        }

    }


}
