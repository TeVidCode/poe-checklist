package org.tevid.todo_list.profile;


public  interface ProfileChangeListener {
	public void onProfileSelected(Profile profile);
	public void onProfileSetActive(Profile profile);
}