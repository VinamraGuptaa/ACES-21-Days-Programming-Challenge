package com.example.projectmanag

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        nav_View.setNavigationItemSelectedListener(this)

    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(Drawer_Layout.isDrawerOpen(GravityCompat.START)){
                Drawer_Layout.closeDrawer(GravityCompat.START)
            }
            else{
            Drawer_Layout.openDrawer(GravityCompat.START)

        }
    }

    override fun onBackPressed() {
        if(Drawer_Layout.isDrawerOpen(GravityCompat.START)){
            Drawer_Layout.closeDrawer(GravityCompat.START)
        }
        else{
           doubleBackToExit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                Toast.makeText(this,"Go to Profile",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,Intro_Activity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
            }


        }
        Drawer_Layout.closeDrawer(GravityCompat.START)
        return true

    }

}