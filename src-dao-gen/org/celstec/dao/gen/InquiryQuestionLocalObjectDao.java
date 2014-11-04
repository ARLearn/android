package org.celstec.dao.gen;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import org.celstec.dao.gen.InquiryQuestionLocalObject;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table INQUIRY_QUESTION_LOCAL_OBJECT.
*/
public class InquiryQuestionLocalObjectDao extends AbstractDao<InquiryQuestionLocalObject, String> {

    public static final String TABLENAME = "INQUIRY_QUESTION_LOCAL_OBJECT";

    /**
     * Properties of entity InquiryQuestionLocalObject.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Identifier = new Property(0, String.class, "identifier", true, "IDENTIFIER");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(2, String.class, "description", false, "DESCRIPTION");
        public final static Property InquiryId = new Property(3, Long.class, "inquiryId", false, "INQUIRY_ID");
    };

    private DaoSession daoSession;

    private Query<InquiryQuestionLocalObject> inquiryLocalObject_QuestionsQuery;

    public InquiryQuestionLocalObjectDao(DaoConfig config) {
        super(config);
    }
    
    public InquiryQuestionLocalObjectDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'INQUIRY_QUESTION_LOCAL_OBJECT' (" + //
                "'IDENTIFIER' TEXT PRIMARY KEY NOT NULL ," + // 0: identifier
                "'TITLE' TEXT," + // 1: title
                "'DESCRIPTION' TEXT," + // 2: description
                "'INQUIRY_ID' INTEGER);"); // 3: inquiryId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'INQUIRY_QUESTION_LOCAL_OBJECT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, InquiryQuestionLocalObject entity) {
        stmt.clearBindings();
 
        String identifier = entity.getIdentifier();
        if (identifier != null) {
            stmt.bindString(1, identifier);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(3, description);
        }
 
        Long inquiryId = entity.getInquiryId();
        if (inquiryId != null) {
            stmt.bindLong(4, inquiryId);
        }
    }

    @Override
    protected void attachEntity(InquiryQuestionLocalObject entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public InquiryQuestionLocalObject readEntity(Cursor cursor, int offset) {
        InquiryQuestionLocalObject entity = new InquiryQuestionLocalObject( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // identifier
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // description
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3) // inquiryId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, InquiryQuestionLocalObject entity, int offset) {
        entity.setIdentifier(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setInquiryId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(InquiryQuestionLocalObject entity, long rowId) {
        return entity.getIdentifier();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(InquiryQuestionLocalObject entity) {
        if(entity != null) {
            return entity.getIdentifier();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "questions" to-many relationship of InquiryLocalObject. */
    public List<InquiryQuestionLocalObject> _queryInquiryLocalObject_Questions(Long inquiryId) {
        synchronized (this) {
            if (inquiryLocalObject_QuestionsQuery == null) {
                QueryBuilder<InquiryQuestionLocalObject> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.InquiryId.eq(null));
                inquiryLocalObject_QuestionsQuery = queryBuilder.build();
            }
        }
        Query<InquiryQuestionLocalObject> query = inquiryLocalObject_QuestionsQuery.forCurrentThread();
        query.setParameter(0, inquiryId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getInquiryLocalObjectDao().getAllColumns());
            builder.append(" FROM INQUIRY_QUESTION_LOCAL_OBJECT T");
            builder.append(" LEFT JOIN INQUIRY_LOCAL_OBJECT T0 ON T.'INQUIRY_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected InquiryQuestionLocalObject loadCurrentDeep(Cursor cursor, boolean lock) {
        InquiryQuestionLocalObject entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        InquiryLocalObject inquiryLocalObject = loadCurrentOther(daoSession.getInquiryLocalObjectDao(), cursor, offset);
        entity.setInquiryLocalObject(inquiryLocalObject);

        return entity;    
    }

    public InquiryQuestionLocalObject loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<InquiryQuestionLocalObject> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<InquiryQuestionLocalObject> list = new ArrayList<InquiryQuestionLocalObject>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<InquiryQuestionLocalObject> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<InquiryQuestionLocalObject> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}