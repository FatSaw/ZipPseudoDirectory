package me.bomb.zippseudodirectory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class PseudoDirectory {
	public static void main(String[] args) throws IOException {
		String srcfilepath = null, targetfilepath=null, startswith=null, endswith=null;
		for(String arg : args) {
			if(arg.startsWith("src=")) {
				srcfilepath = arg.substring(4);
			}
			if(arg.startsWith("tar=")) {
				targetfilepath = arg.substring(4);
			}
			if(arg.startsWith("starts=")) {
				startswith = arg.substring(7);
			}
			if(arg.startsWith("ends=")) {
				endswith = arg.substring(5);
			}
		}
		StringBuffer reason = new StringBuffer("Required options: ");
		if(srcfilepath==null) {
			reason.append("src=");
			reason.append(" ");
		}
		if(targetfilepath==null) {
			reason.append("tar=");
			reason.append(" ");
		}
		if(startswith==null) {
			reason.append("starts=");
			reason.append(" ");
		}
		if(endswith==null) {
			reason.append("ends=");
			reason.append(" ");
		}
		if(srcfilepath==null||targetfilepath==null||startswith==null||endswith==null) {
			System.out.println(reason.toString());
			return;
		}
		reason = null;
		File srcfile = new File(srcfilepath), destfile = new File(targetfilepath);
		
		
		//System.out.println("Start ZipEntryReader!");
		HashMap<String, String> rename = new HashMap<String, String>();
		ZipEntryReader zer = new ZipEntryReader(srcfile);
		Set<String> entrys = null;
		byte ert = 0;
		while ((entrys = zer.getEntrys()) == null) {
			if(++ert==0) break;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		if(entrys==null) {
			//System.out.println("Timeout ZipEntryReader!");
			return;
		}
		//System.out.println("End ZipEntryReader!");
		int count = 0;
		for(String entry : entrys) {
			if(entry.startsWith(startswith) && entry.endsWith(endswith)) {
				rename.put(entry, entry.concat("/"));
				++count;
			}
		}

		//System.out.println("Start ZipEntryRenamer!");
		ZipEntryRenamer zipentryrenamer = new ZipEntryRenamer(srcfile, destfile, rename);
		ert = 0;
		while (!zipentryrenamer.isOk()) {
			if(++ert==0) break;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		//System.out.println("End ZipEntryRenamer!");
		System.out.println("Renamed ".concat(Integer.toString(count).concat(" entrys!")));
	}
}
