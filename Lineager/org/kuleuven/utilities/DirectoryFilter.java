package org.kuleuven.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class DirectoryFilter extends FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname.isDirectory()) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {

		return "Only Directories";
	}
}
