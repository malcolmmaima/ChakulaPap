package maima.chakulapap.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import maima.chakulapap.Fragment.FoodFragment;
import maima.chakulapap.Fragment.DrinksFragment;
import maima.chakulapap.Fragment.ExtrasFragment;
import maima.chakulapap.LoginActivity;
import maima.chakulapap.R;
import maima.chakulapap.ViewPagerAdapter;


public class CustomerActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    //Fragments

    DrinksFragment drinksFragment;
    FoodFragment foodFragment;
    ExtrasFragment extrasFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Set title to name of logged in user
        Intent in = getIntent();
        String tv1= in.getExtras().getString("Welcome");
        setTitle(tv1);

        //Set icon on action bar
        getSupportActionBar().setLogo(R.drawable.icon_user);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Floating email icon for help purposes
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","malcolmmaima@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ChakulaPap: ");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_logout:
                Intent logout=new Intent(this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logout);
                finish();
                return true;

            case R.id.action_refresh:
                Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        foodFragment =new FoodFragment();
        drinksFragment =new DrinksFragment();
        extrasFragment =new ExtrasFragment();
        adapter.addFragment(foodFragment,"FOOD");
        adapter.addFragment(drinksFragment,"DRINKS");
        adapter.addFragment(extrasFragment,"EXTRAS");
        viewPager.setAdapter(adapter);
    }

}
