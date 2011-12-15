package my.meta.shoppinglist;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShoppingListExampleActivity extends Activity {
    
	private static final int LIST_EDIT = 1, LIST_ADD = 2;
	ArrayList<String> itemList = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	protected ListView shoppingList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        itemList = LoadData(getString(R.string.savefile_name));
        shoppingList = (ListView) this.findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        shoppingList.setAdapter(adapter);
        registerForContextMenu(shoppingList);
    }
    
    // Context menu creator
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.remove_item:
        	RemoveItem(info.position);
            return true;
        case R.id.edit_item:
        	Intent t = new Intent("my.meta.shoppinglist.inputDialog");
        	t.putExtra("title", "Edit");
        	t.putExtra("value", itemList.get(info.position));
        	t.putExtra("item_position", info.position);
        	this.startActivityForResult(t, LIST_EDIT);
            return true;
        }
        return false;
    }

    // Options menu creator
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.new_item:
        	Intent t = new Intent("my.meta.shoppinglist.inputDialog");
        	t.putExtra("title", "Add");
        	this.startActivityForResult(t, LIST_ADD);
        	break;
	    }
	    return true;
	}    
	
	// Menu event handler
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	String value = data.getStringExtra("value");
    	if (value != null && value.length() > 0) {
    		switch (requestCode) {
            	case LIST_EDIT:
            		int num = data.getIntExtra("item_position", 0);
            		ModifyItem(num, value);
            		break;
            	case LIST_ADD:
            		AddItem(value);
            		break;
            	default:
            		break;
        	}
    	}
    }
    
    // List item modifiers
    private void AddItem(String item) {
    	itemList.add(item);
		adapter.notifyDataSetChanged();
    }
    private void ModifyItem(int num, String item) {
		itemList.set(num, item);
		adapter.notifyDataSetChanged();
    }
    private void RemoveItem(int num) {
    	itemList.remove(num);
    	adapter.notifyDataSetChanged();
    }
    
    // Save data when backgrounded.
	protected void onPause() {
		SaveData(getString(R.string.savefile_name), itemList);
		super.onPause();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> LoadData(String filename) {
		ArrayList<String> al = new ArrayList<String>();
		InputStream is;
		try {
			is = openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(is);
			al = (ArrayList<String>) ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	
	private void SaveData(String filename, ArrayList<String> data) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(baos);
			out.writeObject(itemList);
			FileOutputStream fo = openFileOutput(filename, Context.MODE_PRIVATE);
			fo.write(baos.toByteArray());
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}