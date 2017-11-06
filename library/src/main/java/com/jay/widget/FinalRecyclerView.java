package com.jay.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by jay on 2017/9/1 上午9:10
 *
 * @author jay
 */
public class FinalRecyclerView extends RecyclerView {

    private HeaderViewAdapter mHeaderViewAdapter;
    private Adapter mAdapter;
    private AdapterDataSetObserver mAdapterDataObserver;
    private View mEmptyView;


    ArrayList<View> mHeaderViews = new ArrayList<>();
    ArrayList<View> mFooterViews = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private boolean mLoading;
    private View mLoadingView;
    private OnLoadingListener mOnLoadingListener;

    public FinalRecyclerView(Context context) {
        this(context, null);
    }

    public FinalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FinalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setItemListener();
    }
    private void setItemListener() {
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {

            private View.OnClickListener innerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getChildAdapterPosition(view);

                    mOnItemClickListener.onItemClick(mAdapter, view, position);
                }
            };


            private View.OnLongClickListener innerOnLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getChildAdapterPosition(view);

                    mOnItemLongClickListener.onItemLongClick(mAdapter, view, position);
                    return true;
                }
            };

            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (view != null) {
                    if (mOnItemClickListener != null) {
                        view.setOnClickListener(innerOnClickListener);
                    }

                    if (mOnItemLongClickListener != null) {
                        view.setOnLongClickListener(innerOnLongClickListener);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(RecyclerView.Adapter adapter, View view, int position);
    }

    /**
     * 添加 HeaderView
     *
     * @param v View
     */
    public void addHeaderView(View v) {
        mHeaderViews.add(v);
        if (mAdapter != null) {
            Adapter adapter = getAdapter();
            if (!(adapter instanceof HeaderViewAdapter)) {
                mHeaderViewAdapter = new HeaderViewAdapter(mAdapter, mHeaderViews, mFooterViews);
                super.setAdapter(mHeaderViewAdapter);
            }
        }
    }

    /**
     * 添加 FooterView
     *
     * @param view View
     */
    public void addFooterView(View view) {
        addFooterView(-1, view);
    }

    private void addFooterView(int index, View view) {
        if (index == -1) {
            mFooterViews.add(view);
        } else {
            mFooterViews.add(index, view);
        }

        if (mAdapter != null) {
            Adapter adapter = getAdapter();
            if (!(adapter instanceof HeaderViewAdapter)) {
                mHeaderViewAdapter = new HeaderViewAdapter(mAdapter, mHeaderViews, mFooterViews);
                super.setAdapter(mHeaderViewAdapter);
            }
        }
    }

    public void setLoadingView(@LayoutRes int resId) {
        View view = LayoutInflater.from(getContext()).inflate(resId, this, false);
        setLoadingView(view);
    }

    public void setLoadingView(View view) {
        addFooterView(view);
        addOnScrollListener();

        mLoadingView = view;
        setLoading(false);
    }

    private void addOnScrollListener() {
        removeOnScrollListener(mOnScrollListener);
        addOnScrollListener(mOnScrollListener);

    }

    public void setLoading(boolean isLoading) {
        mLoading = isLoading;
        if (mLoading) {
            mLoadingView.setVisibility(VISIBLE);
        } else {
            mLoadingView.setVisibility(INVISIBLE);
        }
    }

    public void setOnLoadingListener(OnLoadingListener listener) {
        mOnLoadingListener = listener;
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)
                    && !mLoading
                    && mLoadingView != null
                    && mOnLoadingListener != null
                    && dy > 0) {
                setLoading(true);
                mOnLoadingListener.onLoading();
            }
        }
    };

    public interface OnLoadingListener {
        void onLoading();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mAdapterDataObserver != null) {
            mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        mAdapter = adapter;
        if (mHeaderViews.size() > 0 || mFooterViews.size() > 0) {
            mHeaderViewAdapter = new HeaderViewAdapter(mAdapter, mHeaderViews, mFooterViews);
            super.setAdapter(mHeaderViewAdapter);
        } else {
            super.setAdapter(adapter);
        }

        if (mAdapter != null) {
            mAdapterDataObserver = new AdapterDataSetObserver();
            mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
            updateEmptyStatus(adapter.getItemCount() <= 0);
        }
    }

    /**
     * 设置 EmptyView
     *
     * @param emptyView {@link View}
     */
    public void setEmptyView(View emptyView) {
        if (emptyView == null) {
            throw new NullPointerException("EmptyView 不能为 Null");
        }
        mEmptyView = emptyView;
        if (emptyView.getImportantForAccessibility() == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            emptyView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    /**
     * @return EmptyView
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 更新 EmptyView 显示状态
     *
     * @param empty 是否显示
     */
    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.VISIBLE);
                setVisibility(View.GONE);
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                setVisibility(View.VISIBLE);
            }
        } else {
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
    }

    /*============================================================================================*/

    /**
     * HeaderViewAdapter
     */
    private static class HeaderViewAdapter extends Adapter {

        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK = 0x3 << MODE_SHIFT;

        @ViewType
        static final int ITEM_VIEW_TYPE_HEADER = -1 << MODE_SHIFT;
        @ViewType
        static final int ITEM_VIEW_TYPE_FOOTER = -2 << MODE_SHIFT;

        @IntDef({ITEM_VIEW_TYPE_HEADER, ITEM_VIEW_TYPE_FOOTER})
        @Retention(RetentionPolicy.SOURCE)
        @interface ViewType {
        }

        private static final ArrayList<View> EMPTY_HEADER_VIEWS = new ArrayList<>();
        private final Adapter<ViewHolder> mAdapter;
        private ArrayList<View> mHeaderViews = new ArrayList<>();
        private ArrayList<View> mFooterViews = new ArrayList<>();


        HeaderViewAdapter(Adapter<ViewHolder> adapter, ArrayList<View> headerViews, ArrayList<View> footerViews) {
            mAdapter = adapter;
            if (headerViews == null) {
                mHeaderViews = EMPTY_HEADER_VIEWS;
            } else {
                mHeaderViews = headerViews;
            }

            if (footerViews == null) {
                mFooterViews = EMPTY_HEADER_VIEWS;
            } else {
                mFooterViews = footerViews;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewTypeSpec) {
            int viewType = getViewType(viewTypeSpec);
            switch (viewType) {
                case ITEM_VIEW_TYPE_HEADER:
                    return new ViewHolder(mHeaderViews.get(getPosition(viewTypeSpec))) {
                    };
                case ITEM_VIEW_TYPE_FOOTER:
                    return new ViewHolder(mFooterViews.get(getPosition(viewTypeSpec))) {
                    };
                default:
                    return mAdapter.onCreateViewHolder(parent, viewType);
            }

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Adapter
            if (isItemView(position) && mAdapter != null) {
                int numHeaders = getHeadersCount();
                int adjPosition = position - numHeaders;
                mAdapter.onBindViewHolder(holder, adjPosition);
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
            } else {
                return getFootersCount() + getHeadersCount();
            }
        }

        private int getPosition(int viewTypeSpec) {
            return viewTypeSpec & ~MODE_MASK;
        }

        private int getViewType(int viewTypeSpec) {
            return viewTypeSpec & MODE_MASK;
        }

        private int makeViewTypeSpec(int position, @ViewType int viewType) {
            return (position & ~MODE_MASK | (viewType & MODE_MASK));
        }

        @Override
        public int getItemViewType(int position) {
            final int numHeaders = mHeaderViews.size();
            if (position < numHeaders) {
                return makeViewTypeSpec(position, ITEM_VIEW_TYPE_HEADER);
            }

            // Adapter
            if (mAdapter != null) {
                final int adjPosition = position - numHeaders;
                if (adjPosition < mAdapter.getItemCount()) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            final int footerPosition = position - numHeaders - (mAdapter == null ? 0 : mAdapter.getItemCount());
            return makeViewTypeSpec(footerPosition, ITEM_VIEW_TYPE_FOOTER);
        }

        boolean isItemView(int position) {
            return !isHeaderView(position) && !isFooterView(position);
        }

        boolean isHeaderView(int position) {
            /*
             * header   view    count = 1
             * item     view    count = 5
             */
            return mHeaderViews.size() - 1 >= position;
        }

        boolean isFooterView(int position) {
            /*
             * header   view    count = 1
             * item     view    count = 5
             * footer   view    count = 2
             */
            return mHeaderViews.size() + mAdapter.getItemCount() <= position;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return mFooterViews.size();
        }

        public boolean isEmpty() {
            return mAdapter == null || mAdapter.getItemCount() == 0;
        }

        public Adapter<ViewHolder> getWrappedAdapter() {
            return mAdapter;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (mAdapter != null) {
                //回调本身用户创建的RecyclerView.Adapter类中的方法
                mAdapter.onAttachedToRecyclerView(recyclerView);
            }
            //实现 HeaderView 或 FooterView 始终占用一行
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = (GridLayoutManager) manager;
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    public int getSpanSize(int position) {
                        if (isHeaderView(position) || isFooterView(position)) {
                            return gridManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            if (mAdapter != null) {
                //回调本身用户创建的RecyclerView.Adapter类中的方法
                mAdapter.onViewAttachedToWindow(holder);
            }
            //实现 HeaderView 或 FooterView 始终占用一行
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (isHeaderView(holder.getLayoutPosition()) || isFooterView(holder.getLayoutPosition())) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            if (mAdapter != null) {
                return mAdapter.onFailedToRecycleView(holder);
            } else {
                return false;
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            if (mAdapter != null) {
                mAdapter.onDetachedFromRecyclerView(recyclerView);
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (mAdapter != null) {
                mAdapter.onViewDetachedFromWindow(holder);
            }
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (mAdapter != null) {
                mAdapter.onViewRecycled(holder);
            }
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }
    }

    private class AdapterDataSetObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyDataSetChanged();
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyItemMoved(fromPosition, toPosition);
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Adapter adapter = getAdapter();
            if (adapter instanceof HeaderViewAdapter) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
                updateEmptyStatus(adapter.getItemCount() - mHeaderViews.size() - mFooterViews.size() <= 0);
            } else {
                updateEmptyStatus(adapter.getItemCount() <= 0);
            }
        }
    }
}
