/*******************************************************************************
 * Copyright (c) 2013 Humberto Fraga <xisberto@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Humberto Fraga <xisberto@gmail.com> - initial API and implementation
 ******************************************************************************/
package net.xisberto.phonetodesktop.model;

import android.os.Process;
import net.xisberto.phonetodesktop.Utils;
import net.xisberto.phonetodesktop.database.DatabaseHelper;

public class LocalTask {

	public enum Status {
		ADDED, PROCESSING_UNSHORTEN, PROCESSING_TITLE, READY, SENDING, SENT;
	}
	
	private long local_id;
	private String description, title, google_id;
	private Status status;
	private DatabaseHelper helper;
	
	public LocalTask(DatabaseHelper databaseHelper) {
		this.local_id = -1;
		this.google_id = "";
		this.title = "";
		this.description = "";
		this.status = Status.ADDED;
		this.helper = databaseHelper;
	}
	
	public long getLocalId() {
		return local_id;
	}
	
	public LocalTask setLocalId(long local_id) {
		this.local_id = local_id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public LocalTask setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public LocalTask setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getGoogleId() {
		return google_id;
	}

	public LocalTask setGoogleId(String id) {
		this.google_id = id;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public LocalTask setStatus(Status status) {
		this.status = status;
		return this;
	}
	
	public void persist(PersistCallback callback) {
		int action = (local_id == -1 ? PersistThread.ACTION_INSERT : PersistThread.ACTION_UPDATE);
		new PersistThread(
				helper, this,
				action, callback)
		.start();
	}
	
	public void persist() {
		this.persist(null);
	}
	
	public void delete() {
		new PersistThread(
				helper, this,
				PersistThread.ACTION_DELETE, null)
		.start();
	}
	
	private class PersistThread extends Thread {
		public static final int ACTION_INSERT = 1, ACTION_UPDATE = 2, ACTION_DELETE = 3;
		private DatabaseHelper helper;
		private LocalTask task;
		private int action;
		private PersistCallback callback;
		
		public PersistThread(DatabaseHelper helper, LocalTask task, int action, PersistCallback callback) {
			this.helper = helper;
			this.task = task;
			this.action = action;
			this.callback = callback;
			setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		}

		@Override
		public void run() {
			switch (action) {
			case ACTION_INSERT:
				Utils.log("ACTION_INSERT");
				long id = helper.insert(task);
				task.setLocalId(id);
				break;
			case ACTION_UPDATE:
				Utils.log("ACTION_UPDATE "+task.local_id);
				helper.update(task);
				break;
			case ACTION_DELETE:
				Utils.log("ACTION_DELETE "+task.local_id);
				helper.delete(task);
				break;
			}
			if (callback != null) {
				callback.done();
			}
		}
		
	}
	
	public interface PersistCallback {
		public void done();
	}
}
