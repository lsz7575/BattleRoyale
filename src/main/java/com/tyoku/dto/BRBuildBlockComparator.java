package com.tyoku.dto;

import java.util.Comparator;

public class BRBuildBlockComparator implements Comparator<BRBuildBlock> {

	@Override
	public int compare(BRBuildBlock o1, BRBuildBlock o2) {
		int ret = 0;
		ret = o1.getType().compareTo(o2.getType());
		if(ret == 0){
			ret = o1.getY() - o2.getY();
		}
		if(ret == 0){
			ret = o1.getX() - o2.getX();
		}
		if(ret == 0){
			ret = o1.getZ() - o2.getZ();
		}
		return ret;
	}

}
