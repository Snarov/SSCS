package serverSnarovIA.physicsSnarovIA.materialpointSnarovIA;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

//материальная точка представляет собой точку центра масс тела в трехмерном евклидовом пространстве
//реализует поведение инертной точки в реальном мире
public class MaterialPoint extends Point3f{ 
    
    //поля
    private float mass = 1;                                  //масса (кг)
    private Vector3f force = new Vector3f();                 //вектор результирующей силы (Н)
    private Vector3f velocity = new Vector3f();              //вектор скорости (м/c)
    private Vector3f omega = new Vector3f();                 //вектор угловой скорости (вращение вокруг оси, проходящей через центр масс)(рад/c)
    
    private final Vector3f acceleration = new Vector3f();         //вектор ускорония (косвенное поле, зависит от силы) (м/c^2)
    
    //конструкторы
    public MaterialPoint(){
       super();
    }
    
    public MaterialPoint(float x, float y, float z){
        super(x, y, z);
    }
    
    public MaterialPoint(Point3f point){
        super(point);
    }
    
    public MaterialPoint(float aMass, Vector3f aForce, Vector3f aVelocity, Vector3f aOmega){
        super();
        mass = aMass;
        force = aForce;
        velocity = aVelocity;
        omega = aOmega;
    }
    
    public MaterialPoint(float x, float y, float z, float aMass, Vector3f aForce, Vector3f aVelocity, Vector3f aOmega){
        super(x, y, z);
        mass = aMass;
        force = aForce;
        velocity = aVelocity;
        omega = aOmega;
    }
    
    public MaterialPoint(Point3f point, float aMass, Vector3f aForce, Vector3f aVelocity, Vector3f aOmega){
        super(point);
        mass = aMass;
        force = aForce;
        velocity = aVelocity;
        omega = aOmega;
    }
    
    //методы доступа и модификации
    
    public float getMass() {
        return mass;
    }

    public void setMass(float aMass) {
        mass = aMass;
    }
    
    public Vector3f getForce() {
        return force;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getOmega() {
        return omega;
    }

    public void setForce(Vector3f aForce) {
        force = aForce;
        acceleration.scale(1 / mass, force);
    }

    public void setVelocity(Vector3f aVelocity) {
        velocity = aVelocity;
    }

    public void setOmega(Vector3f aOmega) {
        omega = aOmega;
    }
    
    //поведение
    
    public void addForce(Vector3f additionalForce){                        //прибавляет новую силу к текущему вектору силы
        force.add(additionalForce);
        acceleration.scale(1 / mass, additionalForce);                  //и задает вектор ускорения
    }
    
    public void integrate(long timeMillis){     //высчитывает состояние объекта через timeMillis миллисекунд,используя метод интегрирования Эйлера
        
        float timeSeconds = timeMillis / 1000;
        //интегрируем ускорение и находим допольнительную составляющую скорости
        Vector3f additionalVelocity = new Vector3f();           //вектор, на который изменится скорость в середине временного интервала
        additionalVelocity.scale(timeSeconds / 2, acceleration);
        velocity.add(additionalVelocity);                   //скорость в середине временного интервала
        
        Vector3f offset = new Vector3f();
        offset.scale(timeSeconds, velocity);             //смещение точки за временной интервал
        add(offset);
        
        velocity.add(additionalVelocity);               //скорость точки в конце временного интервала
    }
            
}
