package com.ags.guideme.db;


public class LugaresDbHelper /*extends SQLiteOpenHelper*/{
	/*private static final int DATABASE_VERSION = 2;
	//private static final org.apache.commons.logging.Log log = PlatformUtil.getLog(LugaresDbHelper.class);
	public static final String LUGAR_DB_NAME = "lugares"; //$NON-NLS-1$
	private static final String LUGAR_TABLE_NAME = "lugares"; //$NON-NLS-1$
	private static final String LUGAR_COL_NAME = "nombre"; //$NON-NLS-1$
	private static final String LUGAR_COL_CATEGORY = "categoria"; //$NON-NLS-1$
	private static final String LUGAR_COL_TEL = "telefono"; //$NON-NLS-1$
	private static final String LUGAR_COL_LAT = "latitud"; //$NON-NLS-1$
	private static final String LUGAR_COL_LON = "longitud"; //$NON-NLS-1$
	private static final String LUGAR_TABLE_CREATE = "CREATE TABLE " + LUGAR_TABLE_NAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
			LUGAR_COL_NAME + " TEXT, " + LUGAR_COL_CATEGORY + " TEXT, " + //$NON-NLS-1$ //$NON-NLS-2$  
			LUGAR_COL_TEL +"int" + LUGAR_COL_LAT + " double, " + LUGAR_COL_LON + " double);"; //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final String FILE_TO_SAVE = "lugares.gpx"; //$NON-NLS-1$
	public static final String FILE_TO_BACKUP = "lugares_bak.gpx"; //$NON-NLS-1$
	
	// externalize ?
		private static final String GPX_GROUP = "Gpx";
		
		private List<Lugar> lugaresFromGPXFile = null;
		private List<Lugar> cachedLugares = new ArrayList<Lugar>();
		private Map<String, List<Lugar>> lugaresGroups = null;
		//private final Context context; 
		//private SQLiteConnection conn;
		
		public LugaresDbHelper(Context context, String nombre, CursorFactory factory, int version){
			super(context, nombre, factory, version);
		}
		
	/*	private SQLiteConnection openConnection(boolean readonly) {
			conn = Context.getSQLiteAPI().getOrCreateDatabase(LUGAR_DB_NAME, readonly);
			if (conn.getVersion() == 0 || DATABASE_VERSION != conn.getVersion()) {
				if (readonly) {
					conn.close();
					conn = context.getSQLiteAPI().getOrCreateDatabase(LUGAR_DB_NAME, readonly);
				}
				if (conn.getVersion() == 0) {
					conn.setVersion(DATABASE_VERSION);
					onCreate(conn);
				} else {
					onUpgrade(conn, conn.getVersion(), DATABASE_VERSION);
				}

			}
			return conn;
		}*/
		
		/*
		@Override
		public void onCreate(SQLiteDatabase db) {
			 db.execSQL(LUGAR_TABLE_CREATE);
			//createCategories(db);
		}
		
		private void createCategories(Context context, SQLiteConnection db){
			addCategoryQuery(context.getString(R.string.lugar_salud_categoria), db);
			addCategoryQuery(context.getString(R.string.lugar_tramites_categoria), db);
			addCategoryQuery(context.getString(R.string.lugar_bares_categoria), db);
			addCategoryQuery(context.getString(R.string.lugar_otros_categoria), db);
		}
		 
		
			/*public void backupSilently() {
				try {
					exportFavorites(FILE_TO_BACKUP);
				} catch (Exception e) {
					//log.error(e.getMessage(), e);
				}
			}
			
			public String exportFavorites(String fileName) {
				File f = new File(context.getAppPath(null), fileName);
				GPXFile gpx = new GPXFile();
				for (Lugar l : getLugares()) {
					if (l.isStored()) {
						WptPt pt = new WptPt();
						pt.lat = l.getLatitud();
						pt.lon = l.getLongitud();
						pt.name = l.getNombre();
						if (l.getCategoria().length() > 0)
							pt.category = l.getCategoria();
						gpx.points.add(pt);
					}
				}
				return GPXUtilities.writeGpxFile(f, gpx, context);
			}*/
			
/*			public List<Lugar> getLugares() {
				checkLugares();
				return cachedLugares;
			}
			
		public boolean addLugar(Lugar l) {
			checkLugares();
			if(l.getNombre().equals("") && lugaresGroups.containsKey(l.getCategoria())){
				return true;
			}
			SQLiteConnection db =  (SQLiteConnection) this.getWritableDatabase();// openConnection(false);
			if (db != null) {
				try {
					db.execSQL(
							"INSERT INTO " + LUGAR_TABLE_NAME + " (" + LUGAR_COL_NAME + ", " + LUGAR_COL_CATEGORY + ", "
									+LUGAR_COL_TEL+","+ LUGAR_COL_LAT + ", " + LUGAR_COL_LON + ")" + " VALUES (?, ?, ?, ?,?)", new Object[] { l.getNombre(), l.getCategoria(),l.getTel(),l.getLatitud(), l.getLongitud() }); //$NON-NLS-1$ //$NON-NLS-2$
					if (!lugaresGroups.containsKey(l.getCategoria())) {
						lugaresGroups.put(l.getCategoria(), new ArrayList<Lugar>());
						if (!l.getNombre().equals("")) {
							addLugar(new Lugar("", l.getCategoria(),0,0,0));
						}
					}
					if (!l.getNombre().equals("")) {
						lugaresGroups.get(l.getCategoria()).add(l);
						cachedLugares.add(l);
					}
					l.setStored(true);
					//backupSilently();
				} finally {
					db.close();
				}
				return true;
			}
			return false;
		}
		
		private void checkLugares(){
			if(lugaresGroups == null){
				lugaresGroups = new TreeMap<String, List<Lugar>>(Collator.getInstance());
				SQLiteConnection db = (SQLiteConnection) this.getWritableDatabase();//openConnection(true);
				if (db != null) {
					try {
					SQLiteCursor query = db.rawQuery("SELECT " + LUGAR_COL_NAME + ", " + LUGAR_COL_CATEGORY + ", "+ LUGAR_COL_TEL +","+ LUGAR_COL_LAT + "," + LUGAR_COL_LON + " FROM " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
							LUGAR_TABLE_NAME, null);
					cachedLugares.clear();
					if (query.moveToFirst()) {
						do {
							String nombre = query.getString(0);
							String cat = query.getString(1);
							if(!lugaresGroups.containsKey(cat)){
								lugaresGroups.put(cat, new ArrayList<Lugar>());
							}
							if (!nombre.equals("")) {
								/*Lugar l = new Lugar(this);
								l.setNombre(nombre);
								l.setCategoria(cat);
								l.setTel((int) query.getInt(2));
								l.setStored(true);
								l.setLatitud(query.getDouble(3));
								l.setLongitud(query.getDouble(4));
								lugaresGroups.get(cat).add(l);*/
		/*					}
						} while (query.moveToNext());
					}
					query.close();
					} finally {
						db.close();
					}
				}
				recalculateCachedLugares();
			}
		}
		
		public boolean editLugar(Lugar l, double lat, double lon) {
			checkLugares();
			SQLiteConnection db = (SQLiteConnection) this.getWritableDatabase();//openConnection(false);
			if (db != null) {
				try {
					db.execSQL(
							"UPDATE " + LUGAR_TABLE_NAME + " SET latitud = ?, longitud = ? WHERE " + whereNameLatLon(), new Object[] { lat, lon, l.getNombre(), l.getLatitud(), l.getLongitud() }); //$NON-NLS-1$ //$NON-NLS-2$ 
					l.setLatitud(lat);
					l.setLongitud(lon);
					//backupSilently();
				} finally {
					db.close();
				}
				return true;
			}
			return false;
		}
		
		public boolean deleteLugar(Lugar l) {
			//checkFavoritePoints();
			SQLiteConnection db = (SQLiteConnection) this.getWritableDatabase();//openConnection(false);
			if (db != null) {
				try {
					db.execSQL(
							"DELETE FROM " + LUGAR_TABLE_NAME + " WHERE categoria = ? AND " + whereNameLatLon(), new Object[] { l.getCategoria(), l.getNombre(),l.getTel() ,l.getLatitud(), l.getLongitud() }); //$NON-NLS-1$ //$NON-NLS-2$
					Lugar lp = buscarLugarParaTodasLasProp(l.getCategoria(), l.getNombre(),l.getTel(), l.getLatitud(), l.getLongitud());
					if (lp != null) {
						lugaresGroups.get(l.getCategoria()).remove(lp);
						cachedLugares.remove(lp);
						lp.setStored(false);
					}
					//backupSilently();
				} finally{
					db.close();
				}
				return true;
			}
			return false;
		}
		
		
		private Lugar buscarLugarParaTodasLasProp(String categoria, String nombre, int tel,double lat, double lon){
			if (lugaresGroups.containsKey(categoria)) {
				for (Lugar l : lugaresGroups.get(categoria)) {
					if (nombre.equals(l.getNombre()) && (lat == l.getLatitud()) && (lon == l.getLongitud())) {
						return l;
					}
				}
			}
			return null;
		}
		
		private String whereNameLatLon() {
			String singleLugar = " " + LUGAR_COL_NAME + "= ? AND " +LUGAR_COL_TEL +"= ? AND "+ LUGAR_COL_LAT + " = ? AND " + LUGAR_COL_LON + " = ?";
			return singleLugar;
		}
		
		public boolean deleteGroup(String group){
			checkLugares();
			Lugar l = new Lugar("",group, 0, 0,0);
			if(deleteLugar(l)){
				lugaresGroups.remove(group);
				//backupSilently();
			}
			return false;
		}
		
		private void addCategoryQuery(String categoria, SQLiteConnection db) {
			db.execSQL("INSERT INTO " + LUGAR_TABLE_NAME +
					" (" +LUGAR_COL_NAME +", " +LUGAR_COL_CATEGORY +", "+LUGAR_COL_TEL+"," +LUGAR_COL_LAT +", " +LUGAR_COL_LON + ")" +
					" VALUES (?, ?, ?, ?, ?)", new Object[] { "", categoria, 0f,0f, 0f }); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		public  Map<String, List<Lugar>> buscarCat(){
			checkLugares();
			return lugaresGroups;
			
		}
		
		/*public void onUpgrade(SQLiteConnection db, int oldVersion, int newVersion) {
			if(oldVersion == 1){
				db.execSQL("ALTER TABLE " + LUGAR_TABLE_NAME +  " ADD " + LUGAR_COL_CATEGORY + " text");
				createCategories(db);
				db.execSQL("UPDATE " + LUGAR_TABLE_NAME + " SET categoria = ?", new Object[] { context.getString(R.string.lugar_otros_categoria)}); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}*/
		
		
		/*private void recalculateCachedLugares(){
			ArrayList<Lugar> temp = new ArrayList<Lugar>();
			for(List<Lugar> f : lugaresGroups.values()){
				temp.addAll(f);
			}
			cachedLugares = temp;
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion == 1){
				db.execSQL("ALTER TABLE " + LUGAR_TABLE_NAME +  " ADD " + LUGAR_COL_CATEGORY + " text");
				//createCategories(db);
				db.execSQL("UPDATE " + LUGAR_TABLE_NAME + " SET categoria = ?", new Object[] {R.string.lugar_otros_categoria}); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
		}*/

		
		
}
