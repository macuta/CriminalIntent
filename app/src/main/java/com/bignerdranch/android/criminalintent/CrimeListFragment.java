package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.text.format.DateFormat;
import java.util.List;

public class CrimeListFragment extends Fragment
{
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int savedPosition;
    private static final String SAVED_POSITION = "SAVED_POSITION";

    @Override
    public void onSaveInstanceState(Bundle onSavedInstanceState) {
        super.onSaveInstanceState(onSavedInstanceState);
        onSavedInstanceState.putSerializable(SAVED_POSITION, savedPosition);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            savedPosition = getArguments().getInt(SAVED_POSITION);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemChanged(savedPosition);
        }
    }

    private class CrimeHolder extends CrimeHolderBind
    {
        private CrimeHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }
    }

    private class CrimeHolderPolice extends CrimeHolderBind implements View.OnClickListener
    {
        private Button mPoliceButton;

        private CrimeHolderPolice(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_crime_police, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mPoliceButton = (Button) itemView.findViewById(R.id.button_police);

            mPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(
                            getActivity(),
                            "dial...",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolderBind>
    {
        private List<Crime> mCrimes;

        private CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolderBind onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            if (viewType == 1) {
                return new CrimeHolderPolice(layoutInflater, parent);
            }

            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolderBind holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public int getItemViewType(int position)
        {
            if (mCrimes.get(position).isPoliceContactNeeded()) {
                return 1;
            }

            return 0;
        }
    }

    protected class CrimeHolderBind extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected TextView mTitleTextView;
        protected TextView mDateTextView;
        protected Crime mCrime;
        protected ImageView mSolvedImageView;

        private CrimeHolderBind(View itemView) {
            super(itemView);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            CharSequence stringDate = DateFormat.format("EEEE, MMM, dd, yyyy", mCrime.getDate());
            mDateTextView.setText(stringDate);
            if (mSolvedImageView != null) {
                mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            savedPosition = getAdapterPosition();
            startActivity(intent);
        }
    }
}
