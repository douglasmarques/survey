package br.com.futusteps.survey;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import br.com.futusteps.survey.data.repository.UserRepositories;
import br.com.futusteps.survey.data.repository.UserRepository;
import br.com.futusteps.survey.ui.base.BaseActivity;
import br.com.futusteps.survey.ui.createsurvey.CreateSurveyActivity;
import br.com.futusteps.survey.ui.login.LoginActivity;
import br.com.futusteps.survey.ui.splash.SplashActivity;
import br.com.futusteps.survey.ui.surveylist.SurveyListFragment;
import br.com.futusteps.survey.ui.view.Alert;
import br.com.futusteps.survey.util.StringUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @Bind(R.id.nav_view)
    protected NavigationView mNavigationView;

    @Bind(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dependencyInjection();
        prepareView(savedInstanceState);
    }

    private void prepareView(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);

        if (null == savedInstanceState) {
            mNavigationView.setCheckedItem(R.id.nav_surveys);
            initFragment(R.id.contentFrame, SurveyListFragment.newInstance());
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            TextView userName = ButterKnife.findById(mNavigationView.getHeaderView(0), R.id.userName);
            TextView email = ButterKnife.findById(mNavigationView.getHeaderView(0), R.id.email);
            if(StringUtils.isNotBlank(user.getDisplayName())){
                userName.setText(user.getDisplayName());
            }
            if(StringUtils.isNotBlank(user.getEmail())){
                email.setText(user.getEmail());
            }

            if(user.getPhotoUrl() != null){
                ImageView userImage = ButterKnife.findById(mNavigationView.getHeaderView(0), R.id.userImage);
                Picasso.with(this)
                        .load(user.getPhotoUrl())
                        .transform(new CircleTransform())
                        .into(userImage);
            }
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateSurveyActivity.class));
            }
        });

    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    private void dependencyInjection() {
        repository = UserRepositories.getInMemoryRepoInstance();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stores) {
            // next project phase
        } else if (id == R.id.nav_surveys) {
            // it comes selected
        } else if (id == R.id.nav_logoff) {
            logout();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Alert.showConfirm(this, R.string.drawer_logout_title, R.string.leave,
                R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                        finish();
                                    }
                                });

                    }
                });
    }

}
