package ca.marklauman.tools;

import android.database.Cursor;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.ListAdapter;

/** An interface that covers most things needed to load and display a cursor from a database.
 *  Classes that implement this can be used in ListView.setAdapter(),
 *  LoaderManager.initLoader() and SimpleCursorAdapter.setViewBinder()
 *  @author Mark Lauman */
@SuppressWarnings({"unused"})
public interface CursorHandler  extends ListAdapter, ViewBinder, LoaderCallbacks<Cursor> {}