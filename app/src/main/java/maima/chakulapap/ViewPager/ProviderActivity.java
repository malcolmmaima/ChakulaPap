package maima.chakulapap.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import maima.chakulapap.Fragment.FragmentAddMenu;
import maima.chakulapap.Fragment.MyMenu;
import maima.chakulapap.Fragment.OrdersFragment;
import maima.chakulapap.LoginActivity;
import maima.chakulapap.R;
import maima.chakulapap.ViewPagerAdapter;

public class ProviderActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;


    ViewPagerAdapter adapter;

    //Fragments
    MyMenu myMenuFragment;
    OrdersFragment ordersFragment;
    FragmentAddMenu fragmentAddMenu;

    //Orders counter
    TextView orders_counter;

    String[] tabTitle={"ORDERS","ADD MENU","EMPTY"};
    int[] unreadCount={10,5,0};
    int orders = 0;

    public static String STRING_KEY = "back_from_cam";
    public static String VALUE1 = "yes";
    private String _extra;
    private int _fragmentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

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

        //Setup orders counter at top left corner
        orders_counter = (TextView) findViewById(R.id.tv_count);
        orders_counter.setText(""+orders);

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

            case R.id.action_refresh:
                Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_logout:
                Intent logout=new Intent(this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logout);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentAddMenu =new FragmentAddMenu();
        myMenuFragment =new MyMenu();
        ordersFragment = new OrdersFragment();
        adapter.addFragment(ordersFragment,"ORDERS");
        adapter.addFragment(fragmentAddMenu,"ADD MENU");
        adapter.addFragment(myMenuFragment,"MY MENU");
        viewPager.setAdapter(adapter);

    }


}
