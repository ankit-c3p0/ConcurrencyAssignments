package com.custom.classes;

public class TestConcurrentHashMap {
	
	public static void main(String [] args) {
		final CustomConcurrentHashMap<Integer, Integer> map = new CustomConcurrentHashMap<Integer, Integer>(5);
//		map.put("Kumar", "1000");
//		map.put("Shree", "1222");
//		map.put("pavi", "12222");
//		map.putIfAbsent("pavi", "333");
//		System.out.println(map.get("pavi"));
//		System.out.println(map.toString());
		Runnable r1 = new Runnable() {
			int i = 0 ;
			int j = 6;
			public void run() {
				while(i<6) {
					map.put(i++,j--);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						
					}
				}
				System.out.println("Thread Done :" +Thread.currentThread().getName());
				System.out.println("Map : " +map.toString());			
			}
		};
		
		Runnable r2 = new Runnable() {
			int i = 0 ;
			int j = 6;
			public void run() {
				while(i<6) {
					map.put(i++,j--);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						
					}
				}
				System.out.println("Thread Done :" +Thread.currentThread().getName());
				System.out.println("Map : " +map.toString());
			}
	  
		};
		
		Thread t1 = new Thread(r1,"t1");
		Thread t2 = new Thread(r2,"t2");
		t1.start();
		t2.start();

	}

}
