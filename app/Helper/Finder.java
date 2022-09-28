package Helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.Application;
import io.ebean.*;
import io.ebean.Query;
import io.ebean.annotation.Formula;
import play.Logger;
import play.data.Form;
import play.libs.Json;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class Finder<T> {


    private final Class<T> type;
    private final String serverName;
    private final String currentProperty;
    private ExpressionList<T> list = null;
    private Expression expression = null;
    private Expression pageExp = null;
    private JsonNode tokenNode = null;
    private Long currentId = null;
    private int limit = 50;
    public String error = "";
    private String saveRoute = "/";
    private String buttonName = "Save Change";
    private String formTitle = "Save form";
    private List<String> columns = new ArrayList<>();
    private List<String[]> columnsAll = new ArrayList<>();
    private List<Class<?>> subClasses = new ArrayList<>();
    private List<Class<?>> subClassesOneToOne = new ArrayList<>();
    private List<String> excepted = new ArrayList<>();
    private List<String> disabled = new ArrayList<>();
    private String formDisable = "";
    private String datePattern;
    private String targetQueryId = null;
    private JsonNode targetNode = null;
    private JsonNode fieldsJson = null;
    private List<T> currentList = null;
    private Class<?> formType;
    private List<Class<?>> cList = new ArrayList<>();
    private Query<?> reportQuery = null;
    private UpdateQuery<T> update = null;
    private boolean isNewDisabled = false;
    SimpleDateFormat format;

    private String orderKey = "id";
    private String sqlDone;


    public Finder(Class<T> type) {
        this.type = type;
        this.formType = type;
        this.serverName = "default";
        this.defaultDatePattern();
        this.currentProperty = idColumn();
        this.defaultValueSql();
    }

    public Finder<T> setSaveRoute(String saveRoute) {
        this.saveRoute = saveRoute;
        return this;
    }

    private void  defaultDatePattern(){
        this.setDatePattern("yyyy-MM-dd");
    }

    private void  nullDatePattern(){
        this.setDatePattern("");
    }

    public void setDatePattern(String datePattern){
        this.datePattern = datePattern;
        this.format = new SimpleDateFormat(datePattern);
    }

    public Finder<T> setTitle(String title) {
        this.formTitle = title;
        return this;
    }

    public void update(Long id) {

        if (this.update == null) return;

        final String idColumn = idColumn();

        ExpressionList<T> eq = this.update.where().eq(idColumn, id);

        try {
            eq.update();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.update = null;
    }

    public void update() {
        if (isCurrentIdNull()) return;
        update(currentId);
    }

    private void defaultValueSql() {
        this.sqlDone = "No query Executed yet";
    }

    public Finder<T> setFormType(Class<?> formType) {
        this.formType = formType;
        return this;
    }

    public String getSqlQuery() {
        return sqlDone;
    }

    public Finder<T> setCurrentList(List<T> currentList) {
        this.currentList = currentList;
        return this;
    }


    public Finder<T> setOrderKey(String orderKey) {
        this.orderKey = orderKey;
        return this;
    }

    public Finder<T> setTarget(String targetQueryId) {
        this.targetQueryId = targetQueryId;
        return this;
    }

    public Finder<T> disableNew() {
        this.isNewDisabled = true;
        return this;
    }

    public Finder<T> setTargetNode(JsonNode node) {
        this.targetNode = node;
        return this;
    }

    public Finder<T> setButtonName(String buttonName) {
        this.buttonName = buttonName;
        return this;
    }

    public Finder<T> setPageExp(Expression pageExp) {
        this.pageExp = pageExp;
        return this;
    }

    public void setToken(JsonNode tokenNode) {
        this.tokenNode = tokenNode;
    }

    public Finder<T> setExpLst(ExpressionList<T> list) {
        this.list = list;
        return this;
    }

    private EbeanServer server() {
        return Ebean.getServer(serverName);
    }

    private Query<T> f() {
        return server().find(type);
    }

    public List<T> list() {
        return this.order(f());
    }

    public ExpressionList<T> query() {
        return f().where();
    }

    public T obj(Long id) {
        _setId(id);
        return server().find(type, id);
    }

    public T single() {
        return queryT().setMaxRows(1).findOne();
    }

    public List<T> all() {
        return order(queryT());
    }

    private List<T> iAll(int a) {
        Query<T> tQuery = this.setPagination(queryT(), a);
        this.reset();
        return this.order(tQuery);
    }

    public List<T> all(int a) {
        return iAll(a);
    }


    private boolean existSingle(String cl, Object val) {
        return query().eq(cl, val).findCount() > 0;
    }

    private void existSingleList(String cl, Object val) {
        createQuery();
        list.add(Expr.eq(cl, val));
    }

    public ObjectNode createEl(String type, Object value, String display, String name) {
        return createEl(type, value, display, name, false);
    }

    public ObjectNode createEl(String type, Object value, String display, String name, boolean isNumber) {
        ObjectNode node = Json.newObject();
        node.put("type", type);
        node.put("value", value.toString());
        node.put("name", name);
        node.put("label", display);
        if (isNumber) node.put("isNumber", true);

        return node;
    }

    public ObjectNode createEl(String type, Object value, String display) {
        return createEl(type, value, display, display);
    }

    public ObjectNode createEl() {
        return createEl("text", "", "Form text");
    }


    public boolean exist(String cl, Object val) {
        return isCurrentIdNull() ? existSingle(cl, val) : existWithId(cl, val);
    }

    public boolean exist(String cl, Object val,Long id) {
        if( id != null ) this._setId(id);
        return exist(cl,val);
    }

    public Finder<T> existList(String cl, Object val) {
        existSingleList(cl, val);
        return this;
    }

    public boolean executeExist() {
        boolean b = f().where().addAll(checkedId()).findCount() > 0;
        reset();
        return b;
    }

    public boolean executeExist(Long id) {
        this._setId(id);
        return executeExist();
    }

    public Finder<T> reset() {
        this.list = null;
        this.expression = null;
        this.pageExp = null;
        this.currentId = null;
        this.reportQuery = null;
        return this;
    }

    public Finder<T> enable(String str) {
        this.excepted.add(str);
        return this;
    }

    public Finder<T> disable(String str) {
        this.disabled.add(str);
        return this;
    }

    public Finder<T> disableFormKey(String str) {
        this.formDisable = str;
        return this;
    }


    private boolean isPrimitive(Class<?> t){
        return t == double.class || t == long.class || t == int.class || t == short.class || t == float.class;
    }

    private boolean isNumber(Class<?> type){
        return Number.class.isAssignableFrom(type) || this.isPrimitive(type);
    }


    private ExpressionList<T> checkedId() {
        if (bothAllowed()) {
            return list.ne(currentProperty, currentId);
        }
        return list;
    }

    public T object() {
        T unique = f().where().addAll(checkedId()).setMaxRows(1).findOne();
        reset();
        return unique;
    }

    private void _setId(Long id) {
        this.currentId = id;
    }

    private boolean isCurrentIdNull() {
        return this.currentId == null;
    }

    private boolean isCurrentPropNull() {
        return this.currentProperty == null;
    }

    private boolean bothAllowed() {
        return !isCurrentPropNull() && !isCurrentIdNull();
    }

    private boolean existWithId(String cl, Object val) {
        try {
            int i = query().ne(currentProperty, currentId).eq(cl, val).findCount();
            this.reset();
            return i > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public JsonNode allColumns() {
        if (columns.isEmpty()) this.setColumns();

        return Json.toJson(columns);
    }

    public T formData() {
        Form<T> form = SuperBase.fFactory.form(type).bindFromRequest();

        return formData(form);
    }

    public T formData(Long id) {
        _setId(id);
        return formData();
    }


    public boolean checkExist(Object object) {
        return checkExist(object,null);
    }


    public boolean checkExist(Object object,Long id) {
        error = "Entry existed";
        try {
            for (Method method : object.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Exist.class)) {
                    method.setAccessible(true);
                    Object invoke = method.invoke(object,id);
                    Exist exist = method.getAnnotation(Exist.class);
                    if (!exist.error().equals("")) error = exist.error();
                    boolean b = invoke.getClass() == Boolean.class;
                    return b && (boolean) invoke;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            error = e.getMessage() + " " + e.getClass().getName();
        }
        return false;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private T formData(Form<T> form) {
        T t = newBean();

        this.update = server().update(type);

        if (t == null) return null;

        for (Field field : this.getFields()) {

            if (isId(field)) continue;

            boolean attr = isAttrEntity(field);
            String fieldName = field.getName();
            String value = null;
            Optional<String> vk = form.field(fieldName).getValue();
            if (vk.isPresent()) {
                value = vk.get();
            }
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            boolean hasForm = field.isAnnotationPresent(FormProp.class) && !field.isAnnotationPresent(Formula.class);

            if( !hasForm ) continue;

            FormProp prop = field.getAnnotation(FormProp.class);
            boolean capital = prop.Aa();

            try {
                if (attr) {
                    String idColumn = idColumn(fieldType);

                    if (idColumn == null) continue;

                    String col = fieldName + "." + idColumn;
                    Optional<String> optional = form.field(col).getValue();
                    value = optional.orElse(value);
                    if (isNumeric(value)) {
                        assert value != null;
                        Object o = server().find(fieldType, Long.parseLong(value));
                        field.set(t, o);
                        this.update = this.update.set(col, value);
                    }
                }else if( prop.isUpload() ){
                    String upload = SuperBase.upload(fieldName);
                    field.set(t, upload);
                    this.update = this.update.set(fieldName, upload);
                } else if (value != null && isAttr(field)) {
                    Object val = value;
                    boolean isInt = fieldType == int.class || Integer.class == fieldType;
                    boolean isD = fieldType == double.class || Double.class == fieldType;
                    boolean isL = fieldType == long.class || Long.class == fieldType;
                    boolean isBool = fieldType == boolean.class || Boolean.class == fieldType;
                    boolean isDate = fieldType == Date.class;
                    val = isInt ? Integer.valueOf(value) : val;
                    val = isD ? Double.valueOf(value) : val;
                    val = isL ? Long.valueOf(value) : val;
                    val = isBool ? Boolean.valueOf(value) : val;



                    if (capital && fieldType == String.class) {
                        val = value.toUpperCase();
                    }

                    if( isDate ){
                        Date date = parsedDate(value);
                        field.set(t, date);
                        Logger.warn(date.toString());
                        String sql = fieldName+"='"+value+"'";
                        this.update = this.update.setRaw(sql);
                    }else {
                        field.set(t, val);
                        this.update = this.update.set(fieldName, val);
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException i) {
                System.out.println(i.getMessage());
            }
        }
        return t;
    }


    public void sendEmail(Consumer<T> consumer){

    }

    public void callback(Consumer<T> consumer) {
        //if (this.isCurrentIdNull())
            //consumer.accept(newBean());
        Finder<T> tFinder = this;
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(tFinder.getClass().getClassLoader());
            try {
                consumer.accept(newBean());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }, Executors.newSingleThreadExecutor()).exceptionally(exc -> {
            exc.printStackTrace();
            return null;
        });
    }

    private Date parsedDate(String o) {
        try {
            defaultDatePattern();
            return this.format.parse(o);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private String formatDate(String o){
        return format.format(parsedDate(o));
    }

    private String formatDateInsert(String o){
        setDatePattern("yyyy-MM-dd hh:mm:ss");
        String s = formatDate(o);
        defaultDatePattern();
        return s;
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Long getLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    private String idColumn() {
        return isCurrentPropNull() ? idColumn(type) : currentProperty;
    }

    private String idColumn(Class<?> type) {
        for (Field f : getFields(type) ) {
            if (isId(f)) return f.getName();
        }
        return null;
    }

    private static boolean isId(Field f) {
        return f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(EmbeddedId.class);
    }

    public Finder<T> isSearch(String query) {

        if (columns.isEmpty()) this.setColumns();

        for (String s : columns) {
            this.sChain(s, query);
        }
        return this;
    }

    private void setColumns() {
        Class<T> type = this.type;
        this.tName(type);
    }

    private static boolean isAttr(Field field) {
        boolean annotationPresent = field.isAnnotationPresent(Transient.class);
        boolean aFinal = Modifier.isFinal(field.getModifiers());
        boolean aStatic = Modifier.isStatic(field.getModifiers());

        boolean hasAnnotation = field.isAnnotationPresent(OneToOne.class);
        boolean equals = false;
        if (hasAnnotation) {
            OneToOne annotation = field.getAnnotation(OneToOne.class);
            equals = !annotation.mappedBy().equals("");
        }

        boolean one = field.isAnnotationPresent(OneToMany.class);
        boolean equals1 = false;
        if (one) {
            OneToMany annotation = field.getAnnotation(OneToMany.class);
            equals1 = !annotation.mappedBy().equals("");
        }

        return !annotationPresent && !aFinal && !aStatic && !equals && !equals1;
    }

    private static boolean isMethod(Method method) {
        boolean annotationPresent = method.isAnnotationPresent(Transient.class);
        boolean aFinal = Modifier.isFinal(method.getModifiers());
        boolean aStatic = Modifier.isStatic(method.getModifiers());
        boolean property = method.isAnnotationPresent(JsonProperty.class);

        return !annotationPresent && !aFinal && !aStatic && property;
    }

    private static boolean isAttrEntity(Field field) {
        return isAttr(field) && isEntity(field);
    }

    private static boolean isEntity(Field field) {
        return (field.getType().isAnnotationPresent(Entity.class));
    }

    private boolean isManyToOne(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    private boolean isOneToOne(Field field) {
        return field.isAnnotationPresent(OneToOne.class);
    }

    private void tName(Class<?> type) {
        Field[] declaredFields = getFields(type);
        for (Field field : declaredFields) {
            boolean isEntity = isAttrEntity(field);
            boolean isAttr = isAttr(field);
            boolean isAttribute = isAttr && isForSearch(field);
            String name = field.getName();
            if (isEntity) {
                tableName(field, name, name);
                Class<?> fieldType = field.getType();
                if (isManyToOne(field)) {
                    String[] e = {name, name, fieldType.getName(), idColumn(fieldType)};
                    columnsAll.add(e);
                }
                if (doesNotExist(fieldType, subClasses)) {
                    subClasses.add(fieldType);
                }

                if (isOneToOne(field) && notChainDisabled(field)) {
                    subClassesOneToOne.add(fieldType);
                }
            } else if (isAttribute) {
                columns.add(name);
            }

        }
    }

    private boolean isForSearch(Field field) {
        return !field.getType().isAnnotationPresent(Entity.class) && (field.isAnnotationPresent(JsonProperty.class) || (field.isAnnotationPresent(Formula.class) && (field.getType() == String.class)));
    }

    private List<String[]> getAllColumns() {
        if (columnsAll.isEmpty()) this.setColumns();
        return columnsAll;
    }

    public boolean inColList(String s) {
        for (String[] d : getAllColumns()) {
            if (d.length < 4) continue;

            if (d[0].equals(s)) return true;
        }

        return false;
    }

    public void inQuery(String canonicalName, String pkg) {
        whereAm(pkg, canonicalName);
    }

    public String getColWithId(String s) {
        for (String[] d : getAllColumns()) {
            if (d.length < 4) continue;

            if (d[0].equals(s) && d[3] != null) return s + "." + d[3];
        }
        return null;
    }

    public boolean inColInverseList(String s) {
        return inColList(Application.inverse(s));
    }

    private List<Class<?>> whereAm(String pkg) {
        return whereAm(pkg, null);
    }

    private List<Class<?>> whereAm(String pkg, String cName) {
        List<Class<?>> list = ClassFinder.find(pkg);
        List<Class<?>> classList = whereAm(list, type, null, cName);
        cList = new ArrayList<>();
        List<Class<?>> types = ofOneToOneSubTypes(list);
        //List<Class<?>> addIfNot = addIfNot(classList, types);
        return classList;
    }

    private boolean isEntity(Class<?> aClass) {
        return aClass.isAnnotationPresent(Entity.class);
    }

    private List<Class<?>> whereAm(List<Class<?>> list, Class<?> type, Query<?> expression, String canonName) {
        List<Class<?>> classList = new ArrayList<>();
        for (Class<?> aClass : list) {
            if (amIn(aClass, type) && isEntity(aClass) && doesNotExist(aClass, cList)) {
                classList.add(aClass);
                cList.add(aClass);
                Query<?> query = Ebean.find(aClass);

                String f = amInString(aClass, type) + "." + idColumn(aClass);

                query = query.select(f);

                if (expression != null) {
                    query = expression.where().in(idColumn(type), query).query();
                }

                if (canonName != null && !canonName.isEmpty() && canonName.equals(aClass.getName())) {
                    this.reportQuery = query;
                    return classList;
                }

                classList.addAll(whereAm(list, aClass, query, canonName));


            }
        }
        List<Class<?>> types = ofOneToOneSubTypes(list);
        return classList;
    }

    private List<Class<?>> addIfNot(List<Class<?>> classList, List<Class<?>> toBeAdded) {
        List<Class<?>> subClasses = getSubClasses();
        for (Class<?> aClass : toBeAdded) {
            if (doesNotExist(aClass, classList) && doesNotExist(aClass, subClasses) && aClass != type)
                classList.add(aClass);
        }
        return classList;
    }

    private boolean doesNotExist(Class<?> aClass, List<Class<?>> classList) {
        for (Class<?> clazz : classList) {
            if (clazz == aClass) return false;
        }
        return true;
    }

    private List<Class<?>> getSubClasses() {
        if (subClasses.isEmpty()) this.setColumns();

        return subClasses;
    }

    private List<Class<?>> getSubClassesOneToOne() {
        if (subClassesOneToOne.isEmpty()) this.setColumns();

        return subClassesOneToOne;
    }

    private List<Class<?>> ofSubTypes(List<Class<?>> classes) {
        return ofTypeGiven(classes, getSubClasses());
    }

    private List<Class<?>> ofOneToOneSubTypes(List<Class<?>> classes) {
        return ofTypeGiven(classes, getSubClassesOneToOne());
    }

    private List<Class<?>> ofTypeGiven(List<Class<?>> classes, List<Class<?>> aClasses) {
        List<Class<?>> classList = new ArrayList<>();
        for (Class<?> aClass : aClasses) {

            for (Class<?> clazz : classes) {
                if (amIn(clazz, aClass)) classList.add(clazz);
            }

        }
        return classList;
    }

    private boolean amIn(Class<?> aClass, Class<?> type) {
        return amInString(aClass, type) != null;
    }

    private String amInString(Class<?> aClass, Class<?> type) {
        if (aClass == type) return null;

        Field[] fields = getFields(aClass);
        for (Field field : fields) {
            if (field.getType() == type && isAttr(field)) return field.getName();
        }
        return null;
    }

    private boolean amIn(Class<?> aClass) {
        return amIn(aClass, type);
    }

    public JsonNode getModalCols() {
        return _colsModal(type, null);
    }

    private ObjectNode _colsModal(Class<?> type, String s2) {
        ObjectNode newObject = Json.newObject();

        for (Field field : getFields(type) ) {

            String name = field.getName();
            final Class<?> fType = field.getType();

            boolean present = field.isAnnotationPresent(ReportHelper.class);

            boolean entity = isAttrEntity(field);
            final boolean isDate = fType == Date.class;
            if (entity || isDate || present) {
                boolean toOne = isManyToOne(field) || isDate || present;
                String s = s2;
                ReportHelper annotation = field.getAnnotation(ReportHelper.class);
                name = present ? annotation.name() : name;
                s2 = s2 != null ? s2 + "." + name : name;

                boolean b = present && isEntity(annotation.clazz());

                String typeName = b ? annotation.clazz().getName() : fType.getName();


                ObjectNode node = _colsModal(fType, s2);
                node.put("value", name);
                node.put("key", Application.reverse(s2));
                node.put("store", typeName);
                node.put("access", toOne);

                boolean extra = present && !isEntity(annotation.clazz());

                if (extra) node.put("extra", Arrays.toString(annotation.arrayStatus()));

                newObject.set(name, node);
                s2 = s;
            }
        }

        return newObject;
    }

    public List<JsonNode> getAllColumnsHashed() {
        List<JsonNode> nodeList = new ArrayList<>();
        for (String[] string : getAllColumns()) {

            if (string.length < 4) continue;

            String s = (string[0]);
            ObjectNode node = Json.newObject();
            node.put("key", s);
            node.put("value", string[1]);
            node.put("store", string[2]);
            nodeList.add(node);
        }

        return nodeList;
    }

    public JsonNode getWhereAmNode(String pkg) {
        List<Class<?>> classList = whereAm(pkg);

        List<JsonNode> nodeList = new ArrayList<>();

        for (Class<?> clazz : classList) {

            ObjectNode node = Json.newObject();
            String name = clazz.getSimpleName();
            node.put("key", name);
            node.put("value", name);
            node.put("store", clazz.getName());
            nodeList.add(node);
        }

        return Json.toJson(nodeList);
    }

    private void tableName(Field f, String s, String s2) {
        Field[] declaredFields = f.getType().getDeclaredFields();
        int i = 0;
        for (Field field : declaredFields) {


            boolean attr = isAttr(field);


            if (attr) {
                if (i != 0) {
                    s = s2;
                } else {
                    s2 = s;
                }

                boolean present = isForSearch(field);

                final String string = s + "." + field.getName();
                if (present) {
                    columns.add(string);
                }

                boolean isEntity = isEntity(field);

                final Class<?> fieldType = field.getType();
                if (isEntity && isManyToOne(field)) {
                    String[] strings = {string, field.getName(), fieldType.getName(), idColumn(fieldType)};
                    columnsAll.add(strings);
                }

                if (isEntity) {
                    subClasses.add(fieldType);
                }

                if (isEntity && isOneToOne(field) && notChainDisabled(field)) {
                    subClassesOneToOne.add(fieldType);
                }


                if (isEntity) {
                    s = s + "." + field.getName();
                    tableName(field, s, s2);
                }
            }

            i++;
        }
    }

    private boolean notChainDisabled(Field field) {
        boolean present = field.isAnnotationPresent(DisableChain.class);
        if (!present) return true;
        else {
            DisableChain annotation = field.getAnnotation(DisableChain.class);
            return annotation.aClass() != type;
        }

    }

    public int t(int a) {
        return (a - 1) * this.limit;
    }

    private void createQuery() {
        if (list == null) list = query();
    }


    private void sChain(String col, String val) {
        if (expression == null) expression = Expr.icontains(col, val.trim());
        else expression = Expr.or(expression, Expr.icontains(col, val.trim()));
    }

    private Query<T> queryT() {
        ExpressionList<T> tQuery = f().where();
        if (pageExp != null) tQuery.add(pageExp);
        if (list != null) tQuery.addAll(list);
        if (expression != null) tQuery = tQuery.add(expression);

        if (reportQuery != null) tQuery = tQuery.in(idColumn(), reportQuery);

        reset();
        return tQuery.query();
    }

    private Query<T> setPagination(Query<T> query, int a) {
        return query.setFirstRow(t(a)).setMaxRows(this.limit);
    }

    public List<T> search(int a) {
        Query<T> tQuery = this.setPagination(queryT(), a);
        this.reset();
        return this.order(tQuery);
    }

    private String key() {
        return orderKey != null ? orderKey : currentProperty;
    }

    public List<T> order(Query<T> t) {
        String key = key();
        List<T> list;
        t = key != null ? t.order(key + " desc") : t;

        list = t.findList();
        this.sqlDone = t.getGeneratedSql();
        return list;
    }

    public List<T> order(ExpressionList<T> t) {
        return this.order(t.query());
    }

    public int searchCount() {
        createQuery();
        Query<T> tQuery = this.queryT();
        int integer = tQuery.findCount();
        this.reset();
        return counter(integer);
    }

    private int counter(int a) {
        int integer = (int) Math.ceil((float) a / limit);
        return (integer > 1) ? integer : 1;
    }

    public int number() {
        return queryT().findCount();
    }

    public int count() {
        return this.searchCount();
    }


    public JsonNode nodeList(int a) {
        return Json.toJson(all(a));
    }

    public JsonNode nodeList() {
        return Json.toJson(all());
    }

    public JsonNode _structNodeList() {
        ObjectNode node = Json.newObject();
        node.set("data", nodeList());
        node.set("fields", _structure());
        node.put("header", "");

        return node;
    }

    private JsonNode _struct(Class<?> type) {
        ObjectNode node = Json.newObject();
        String display;
        boolean isAvailable = false;
        for (Method method : getMethods(type) ) {
            String name = method.getName();
            display = name;

            if (method.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty property = method.getAnnotation(JsonProperty.class);
                name = property.value().equals("") ? name : property.value();
                display = name;
            }

            if (method.isAnnotationPresent(FormProp.class)) {
                FormProp annotation = method.getAnnotation(FormProp.class);
                display = annotation.display().equals(".") ? display : annotation.display();

                isAvailable = annotation.isReport() || isAvailable;
            }

            isAvailable = isAvailable || method.isAnnotationPresent(JsonProperty.class);


            Class<?> returnType = method.getReturnType();


            if (!isAvailable) continue;

            boolean attr = isMethodJson(method);
            if (attr && isEntity(returnType)) {
                node.set(name, _struct(returnType));
            } else if (attr) {
                node.put(name, display);
            }

        }
        for (Field field : getFields(type) ) {
            String name = field.getName();
            display = name;

            if (field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty property = field.getAnnotation(JsonProperty.class);
                name = property.value().equals("") ? name : property.value();
                display = name;
            }

            if (field.isAnnotationPresent(FormProp.class)) {
                FormProp annotation = field.getAnnotation(FormProp.class);
                display = annotation.display().equals(".") ? display : annotation.display();
            }

            Class<?> fieldType = field.getType();
            boolean attr = isAttrJson(field);
            if (attr && isEntity(field)) {
                node.set(name, _struct(fieldType));
            } else if (attr) {
                node.put(name, display);
            }
        }
        return node;
    }

    private static boolean isAttrJson(Field field) {
        return isAttr(field) && !field.isAnnotationPresent(JsonIgnore.class) && Modifier.isPublic(field.getModifiers()) && !isId(field);
    }

    private static boolean isMethodJson(Method method) {
        return isMethod(method) && Modifier.isPublic(method.getModifiers()) && !method.isAnnotationPresent(NoJsonReport.class);
    }

    private JsonNode _structure() {
        JsonNode node = fieldsJson != null ? fieldsJson : this._struct(type);
        fieldsJson = node;
        return node;
    }

    public JsonNode page() {
        return page(1);
    }

    public boolean inArray(String needle) {
        return inArray(needle, this.excepted);
    }

    private boolean inArray(String needle, List<String> stringList) {

        boolean valid = false;
        for (String i : stringList) {
            if (needle.equals(i)) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    private boolean inDisabled(String needle) {
        return inArray(needle, this.disabled);
    }

    public JsonNode page(int a) {
        ObjectNode node = Json.newObject();
        final List<T> all = this.currentList != null ? this.currentList : this.search(a);

        this.currentList = null;

        Object[] declaredMethods = this.list(getMethods(), getFields(), true);
        int inc = 0;
        ArrayNode nodeList = Json.newArray();
        Class<?> type = null;
        for (T t : all) {
            ArrayNode oNode = Json.newArray();
            for (Object method : declaredMethods) {
                Class<?> aClass = method.getClass();
                Object value = "";
                String name = "";
                boolean annotationPresent = false;
                FormProp annotation = null;
                try {
                    if (aClass == Method.class) {
                        Method method1 = (Method) method;
                        annotationPresent = method1.isAnnotationPresent(FormProp.class);
                        annotation = method1.getAnnotation(FormProp.class);
                        type = method1.getReturnType();


                        annotationPresent = annotationPresent && (annotation.tbl() || annotation.tblOnly());

                        if( annotationPresent ){
                            value = method1.invoke(t);
                            name = method1.getName();
                        }

                    } else if (aClass == Field.class) {
                        Field field = (Field) method;
                        annotationPresent = field.isAnnotationPresent(FormProp.class);
                        annotation = field.getAnnotation(FormProp.class);

                        annotationPresent = annotationPresent && (annotation.tbl() || annotation.tblOnly());

                        if( annotationPresent ){
                            value = field.get(t);
                            name = field.getName();
                        }
                        type = field.getType();
                    }
                    if (value == null) value = "";
                } catch (Exception ignored) {
                    value = "x";
                }
                if (annotationPresent && annotation != null) {

                    ObjectNode objectNode = Json.newObject();
                    objectNode.put("name", annotation.name());


                    boolean bld = isDismissed(name, annotation);

                    String f = annotation.display().equals(".") ? name : annotation.display();
                    value = annotation.str() ? value.toString() : value;
                    putAttr(objectNode, annotation, f, value, true,type);


                    if (bld) oNode.add(objectNode);
                }
            }


            nodeList.add(oNode);
            inc++;
        }

        node.set("form", form());

        node.set("page", nodeList);


        if (isNewDisabled) {
            node.put("newDisabled", true);
        }
        this.isNewDisabled = false;

        this.excepted = new ArrayList<>();
        this.disabled = new ArrayList<>();

        return node;

    }

    private boolean isDismissed(String methodName, FormProp annotation) {
        boolean bld;

        boolean disable = !inDisabled(methodName);

        bld = annotation.dismissed() ? inArray(methodName) && disable : disable;

        return bld;
    }


    private JsonNode formHead() {
        ObjectNode jsonNodes = Json.newObject();

        String fName = "formName";
        String route = "saveRoute";

        jsonNodes.put(route, saveRoute);


        boolean annotationPresent = type.isAnnotationPresent(FormProp.class);
        String name;
        if (annotationPresent) {
            FormProp annotation = type.getAnnotation(FormProp.class);
            name = annotation.formName().equals(".") ? type.getSimpleName() : annotation.formName();
        } else {
            name = this.formTitle;
        }

        jsonNodes.put(fName, name);
        String buttonName = "buttonName";

        jsonNodes.put(buttonName, this.buttonName);

        if (this.tokenNode != null) {
            jsonNodes.set("token", this.tokenNode);
        }

        return jsonNodes;
    }

    public JsonNode form(Long o) {
        _setId(o);
        JsonNode form = form();
        reset();
        return form;
    }

    private void putAttr(ObjectNode node, FormProp annotation, String name, Object idColumn, boolean isTable,Class<?> type) {
        String f = annotation.name();
        idColumn = isTable ? idColumn : idColumn == null ? f : "." + idColumn;
        String formName = isEntity(type) ? name + idColumn : f.equals(".id") ? name : f;
        String attributeName = annotation.attribute();
        String className = annotation.className();
        String idName = annotation.id();
        String typeName = annotation.type();
        String displayName = annotation.display().equals(".") ? name : annotation.display();

        displayName = annotation.tDisplay().equals(".") ? displayName : isTable ? annotation.tDisplay() : displayName;


        node.put("name", formName);
        node.put("label", displayName);
        node.put("type", typeName);
        node.put("attr", attributeName);
        node.put("className", className);
        node.put("id", idName);
        node.put("order", annotation.order());


        if (isTable) {

            if( annotation.isUpload() ){
                idColumn = SuperBase.routeFile(idColumn.toString());
                node.put("file",true);
            }

            node.set("value", Json.toJson(idColumn));
            if (annotation.but())
                node.put("button", true);
            if (annotation.del())
                node.put("delete", true);
            if( annotation.isHtml() )
                node.put("html",true);
            if (annotation.badge()) {
                node.put("badge", true);
                node.put("badgeColor", annotation.bColor());
            }
        } else {

            if (annotation.isCal()) {
                node.put("calendar", true);
            }

            if (annotation.isUpload()) {
                node.put("upload", true);
            }

            if (annotation.isTime()) {
                node.put("timePicker", true);
            }

            if ( isNumber(type) ) {
                node.put("number", true);
            }

            if (annotation.isCheck()) {
                node.put("checked", true);
            }

            if (annotation.isGroup()) {
                node.put("grouped", true);
            }

            if (annotation.escape()) {
                node.put("escape", true);
            }

            if (annotation.isDownload()) {
                node.put("isDownload", true);
            }

            if (annotation.isDisabled()) {
                node.put("disabled", true);
            }
        }
    }

    private Object[] list(Method[] methods, Field[] fields, boolean isTable){
        List<Object> methodList = new ArrayList<>();
        for (Method method : methods) {
            boolean annotationPresent = method.isAnnotationPresent(FormProp.class);
            FormProp annotation = method.getAnnotation(FormProp.class);
            if (annotationPresent && isTable) {
                boolean b = annotation.tbl() || annotation.tblOnly();
                if( b ) methodList.add(method);
            } else if (annotationPresent && !annotation.tblOnly()) {
                methodList.add(method);
            }
        }
        for (Field field : fields) {
            boolean present = field.isAnnotationPresent(FormProp.class);
            FormProp annotation = field.getAnnotation(FormProp.class);
            if (present && isTable) {
                if( annotation.tbl() || annotation.tblOnly() ) methodList.add(field);
            } else if (present && !annotation.tblOnly()) {
                methodList.add(field);
            }
        }
        return methodList.toArray(new Object[0]);
    }

    private Object[] sortTable(Method[] methods, Field[] fields) {

        boolean is = formType == type;

        int p, ac;
        Object[] objects = list(methods,fields,true);
        for (int a = 0; a < objects.length; a++) {
            if (a != 0) {
                for (p = 0; p <= a; p++) {
                    for (ac = a; ac > p; ac--) {

                        boolean isField = objects[ac].getClass() == Field.class;
                        boolean isOldField = objects[ac - 1].getClass() == Field.class;
                        boolean isMethod = objects[ac].getClass() == Method.class;
                        boolean isOldMethod = objects[ac - 1].getClass() == Method.class;


//
//                        int order1;
//                        int order2;
//
//                        if (order1 < order2) {
//                            Object nex = objects[ac];
//                            objects[ac] = objects[ac - 1];
//                            objects[ac - 1] = nex;
//                        }
                    }
                }
            }
        }

        return objects;
    }

    private Field[] getFields(Class<?> type) {

        List<Field> fieldList = new ArrayList<>();
        while (type != null) {
            Field[] declaredFields = type.getDeclaredFields();
            fieldList.addAll(Arrays.asList(declaredFields));
            type = type.getSuperclass();
        }


        return fieldList.toArray(new Field[0]);
    }

    private Method[] getMethods(Class<?> type) {
        List<Method> methodList = new ArrayList<>();

        while (type != null) {
            Method[] declaredMethods = type.getDeclaredMethods();
            methodList.addAll(Arrays.asList(declaredMethods));
            type = type.getSuperclass();
        }

        return methodList.toArray(new Method[0]);
    }

    private Field[] getFields() {
        return getFields(type);
    }

    private Method[] getMethods() {
        return getMethods(type);
    }


    private Object getId(Object object, String col) {
        return getId(object, object.getClass(), col);
    }

    private Object getId(Object object, Class<?> clazz, String col) {
        try {
            return clazz.getDeclaredField(col).get(object);
        } catch (Exception e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) return getId(object, superclass, col);
            else return null;
        }
    }

    private Long getLongId(Object o, String col) {
        try {
            return Long.parseLong(getId(o, col).toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    private T obj() {
        return isCurrentIdNull() ? null : obj(currentId);
    }

    private T newBean() {
        return server().createEntityBean(type);
    }


    public JsonNode form() {
        Method[] declaredMethods = getMethods(formType);
        Field[] declaredFields = getFields(formType);
        ObjectNode jsonNodes = Json.newObject();

        ArrayNode childNode = Json.newArray();

        final Object objectT = obj();
        final Object newObjectT = newBean();


        Object[] objects = this.list(declaredMethods, declaredFields, false);

        FormProp annotation;
        boolean isPresent;
        String name, idColumn;
        Class<?> fieldType;

        for (Object object : objects) {
            ObjectNode node = Json.newObject();

            Object ob;
            if (object.getClass() == Field.class) {
                Field field = (Field) object;
                isPresent = field.isAnnotationPresent(FormProp.class);

                if (!isPresent) continue;

                annotation = field.getAnnotation(FormProp.class);


                fieldType = field.getType();
                name = field.getName();
                idColumn = idColumn(fieldType);
                try {
                    field.setAccessible(true);
                    boolean aStatic = Modifier.isStatic(field.getModifiers());
                    Object dV = aStatic ? field.get(newObjectT) : null;
                    Object o = isCurrentIdNull() ? dV : field.get(objectT);
                    if (o != null) {
                        ob = o;
                        node.put("defaultValue", getLongId(o, idColumn));
                    } else ob = "";

                } catch (Exception e) {
                    ob = "";
                }
            } else if (object.getClass() == Method.class) {
                Method method = (Method) object;
                isPresent = method.isAnnotationPresent(FormProp.class);

                if (!isPresent) continue;


                annotation = method.getAnnotation(FormProp.class);
                fieldType = method.getReturnType();
                name = method.getName();
                idColumn = idColumn(fieldType);
                try {
                    method.setAccessible(true);
                    boolean aStatic = Modifier.isStatic(method.getModifiers());
                    Object dV = aStatic ? method.invoke(newObjectT) : null;
                    Object o = isCurrentIdNull() ? dV : method.invoke(objectT);
                    if (o != null) {
                        ob = o;
                    } else ob = "land";

                } catch (Exception e) {
                    ob = "mad";
                }
            } else continue;

            putAttr(node, annotation, name, idColumn, false,fieldType);
            createMissingNode(fieldType, node, ob, annotation);

            if (!name.equals(this.formDisable)) {
                childNode.add(node);
            }
        }

        String fData = "formData";
        String fHead = "formHead";
        jsonNodes.set(fData, childNode);
        jsonNodes.set(fHead, formHead());

        this.formType = this.type;
        this.formDisable = "";

        return jsonNodes;
    }

    private void createMissingNode(Class<?> type, ObjectNode node, Object o, FormProp formProp) {
        boolean annotation = isEntity(type);
        if (annotation) {
            ExpressionList<?> list = server().find(type).where();
            boolean condition = !formProp.eqProp().equals(".") && !formProp.eqValue().equals(".");

            if (condition) list.add(Expr.eq(formProp.eqProp(), formProp.eqValue()));
            if (this.targetQueryId != null && this.targetNode != null && formProp.id().equals(this.targetQueryId)) {
                node.set("value", this.targetNode);
                this.targetNode = null;
                this.targetQueryId = null;
            } else {
                Object listList;
                int rowCount = list.findCount();
                if (rowCount > 100) {
                    node.put("search", "/k");
                    listList = list.setMaxRows(100).findList();
                } else listList = list.findList();
                if (!formProp.newNull().equals("")) {
                    ObjectNode newNode = Json.newObject();
                    newNode.put("print", formProp.newNull());
                    newNode.put("id", 0);
                    //((List) listList).add(newNode);
                }
                JsonNode jsonNode = Json.toJson(listList);
                node.set("value", jsonNode);
            }
        } else {
            if (type.equals(Date.class)) {
                node.put("value", formatDate(o.toString()));
            } else {
                node.set("value", Json.toJson(o));
            }
        }
    }
}


