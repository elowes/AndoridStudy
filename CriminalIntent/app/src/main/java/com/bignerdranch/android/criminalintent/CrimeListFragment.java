package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;


        public void bind(Crime crime) {
            mCrime = crime;
            DateFormat bf = new SimpleDateFormat("EEEE,MMM,dd,yyyy");
            String format = bf.format(mCrime.getDate());
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(format);
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int resource) {
            super(inflater.inflate(resource, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCrime.getTitle() + "clicked", Toast.LENGTH_SHORT).show();
        }
    }


    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            if(mCrimes.get(position).getRequiresPolice()) {
                return 1;
            } else {
                return 2;
            }
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            int resource;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            switch (i) {
                case 1:
//                    这里取消掉 list_item_crime_rp 的 layout，为了后续和书中样式统一
//                    resource = R.layout.list_item_crime_rp;
//                    break;
                case 2:
                    resource = R.layout.list_item_crime;
                    break;
                default:
                    resource = R.layout.list_item_crime;
            }
            return new CrimeHolder(inflater, viewGroup, resource);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
    }
}
