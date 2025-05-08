<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#FFEE96">

    <!-- Start Page -->
    <LinearLayout
        android:id="@+id/layout_First_Page"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:visibility="visible"
        android:orientation="vertical">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Korean Culture Roblox"
            android:textSize="30sp"
            android:layout_marginBottom="20dp" />

        <Button
            android:id="@+id/btn_Start"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Start" />
    </LinearLayout>

    <!-- In-Game Page -->
    <LinearLayout
        android:id="@+id/layout_InGame"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="invisible"
        android:orientation="vertical">

        <!-- Timer and Score Bar -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="end"
            android:background="#AAFFA3">

            <TextView
                android:id="@+id/TimeDisplay"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="10dp"
                android:textSize="15sp" />

            <TextView
                android:id="@+id/ScoreDisplay"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginEnd="10dp"
                android:textSize="15sp"
                android:text="Score: 0" />

            <Button
                android:id="@+id/btn_InGame_Setting"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginEnd="5dp"
                android:text="setting" />
        </LinearLayout>

        <!-- Game Field -->
        <FrameLayout
            android:id="@+id/inGame_play"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <GridLayout
                android:id="@+id/inGame_field"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:columnCount="7"
                android:rowCount="14" />
        </FrameLayout>
    </LinearLayout>

    <!-- Setting Page -->
    <LinearLayout
        android:id="@+id/layout_SettingPage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="invisible"
        android:orientation="vertical"
        android:padding="20dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Settings"
            android:textSize="20sp"
            android:layout_marginBottom="10dp" />

        <CheckBox
            android:id="@+id/Mute"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Mute" />

        <SeekBar
            android:id="@+id/VolumeControl"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100" />

        <Button
            android:id="@+id/btn_SettingPage_Back"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Back"
            android:layout_marginTop="10dp" />

        <Button
            android:id="@+id/btn_SettingPage_Restart"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Restart"
            android:layout_marginTop="10dp" />

        <Button
            android:id="@+id/btn_SettingPage_Title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Title"
            android:layout_marginTop="10dp" />
    </LinearLayout>

    <!-- End Page -->
    <LinearLayout
        android:id="@+id/layout_EndPage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="invisible"
        android:background="#99ffffff"
        android:orientation="vertical"
        android:gravity="center">

        <TextView
            android:id="@+id/EndScoreLabel"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Score"
            android:textSize="50sp" />

        <TextView
            android:id="@+id/FinalScoreDisplay"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="20dp"
            android:text="0"
            android:textSize="40sp" />

        <Button
            android:id="@+id/btn_EndPage_Again"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginHorizontal="120dp"
            android:text="Again" />

        <Button
            android:id="@+id/btn_EndPage_Title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginHorizontal="120dp"
            android:text="Title" />
    </LinearLayout>

</FrameLayout>
