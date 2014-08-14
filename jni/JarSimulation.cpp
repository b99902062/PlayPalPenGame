#include <jni.h>
#include <android/log.h>
#include <Box2D/Box2D.h>
#include <stdio.h>
#include <vector>

#define PTM_Ratio 100.f
#define FPS 60.f
#define Star_Size 200.f

b2World* m_world;
std::vector< b2Body* > starBodies;
std::vector< b2Body* > starBodies2;

b2Body* generateStarBody(int pos_x, int pos_y){
	float radius = Star_Size/PTM_Ratio/2;
	int body_num = 2;
	int vertex_num = 3;
	b2Body* bodies[2];

	for(int i=0; i<body_num; i++){
		b2Vec2 vertices[3];
		for(int j=0; j<vertex_num; j++){
			vertices[j].Set(radius*cos((j+0.5*i) * 2 * M_PI / vertex_num), radius*sin((j+0.5*i) * 2 * M_PI / vertex_num));
		}

		b2BodyDef starBodyDef;
		starBodyDef.type = b2_dynamicBody;
		starBodyDef.position.Set(pos_x, pos_y);
		starBodyDef.allowSleep = false;

		b2PolygonShape starShape;
		starShape.Set(vertices, vertex_num);

		b2FixtureDef starFixtureDef;
		starFixtureDef.shape = &starShape;
		starFixtureDef.density = 10;
		starFixtureDef.friction = 0.3f;
		starFixtureDef.restitution = 0.2f;

		bodies[i] = m_world->CreateBody(&starBodyDef);
		bodies[i]->CreateFixture(&starFixtureDef);
	}

	b2WeldJointDef jointDef;

	jointDef.bodyA = bodies[0];
	jointDef.bodyB = bodies[1];
	jointDef.localAnchorA = bodies[0]->GetLocalCenter();
	jointDef.localAnchorB = bodies[1]->GetLocalCenter();

	b2RevoluteJoint* joint = (b2RevoluteJoint*)m_world->CreateJoint(&jointDef);
	starBodies.push_back(bodies[0]);
	starBodies2.push_back(bodies[1]);
	return bodies[0];
}

void init() {
	b2Vec2 gravity(0.0f, -100.0f);
	m_world = new b2World(gravity);

	b2BodyDef groundBodyDef;
	b2EdgeShape edgeShape;
	b2FixtureDef groundFixtureDef;
	b2PolygonShape polygonShape;

	groundBodyDef.type = b2_staticBody;
	groundFixtureDef.friction = 0.2f;
	groundFixtureDef.restitution = 0.2f;
	groundFixtureDef.shape = &edgeShape;

	edgeShape.Set( b2Vec2(0/PTM_Ratio,1000/PTM_Ratio), b2Vec2(600/PTM_Ratio,1000/PTM_Ratio) );
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(0/PTM_Ratio,0/PTM_Ratio), b2Vec2(600/PTM_Ratio,0/PTM_Ratio) );
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(0/PTM_Ratio,0/PTM_Ratio), b2Vec2(0/PTM_Ratio,1000/PTM_Ratio) );
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(600/PTM_Ratio,0/PTM_Ratio), b2Vec2(600/PTM_Ratio,1000/PTM_Ratio) );
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

}

int main(int argc, char* argv[]) {
	B2_NOT_USED(argc);
	B2_NOT_USED(argv);

	init();
	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "main:)");

	return 0;
}


extern "C"
jboolean Java_com_example_playpalpengame_JarActivity_putIntoJar (
		JNIEnv* env, jobject thiz, jint objIndex) {

	generateStarBody(300/PTM_Ratio, 700/PTM_Ratio);
	return true;
}

jboolean Java_com_example_playpalpengame_JarActivity_updateAngle (
		JNIEnv* env, jobject thiz, jfloat _xVal, jfloat _yVal, jfloat zVal) {

	float xVal = _xVal;
	float yVal = _yVal;
	m_world->SetGravity(b2Vec2(xVal,yVal));

/*
	char s[100];
	sprintf(s,"%f %f\n",xVal,yVal);
	__android_log_write(ANDROID_LOG_DEBUG, "gravity", s);
*/

	return true;
}

void Java_com_example_playpalpengame_JarActivity_initWorld(JNIEnv* env, jobject thiz){
	init();
	return;
}

jfloatArray Java_com_example_playpalpengame_JarActivity_getPosition(JNIEnv* env, jobject thiz, jint idx){
	int len = 3;
	b2Body* curBody = starBodies.at(idx);
	b2Body* curBody2 = starBodies2.at(idx);

	jfloatArray ret = env->NewFloatArray(len);
	jfloat *body = new jfloat[len];

	body[0] = (curBody->GetWorldCenter().x + curBody2->GetWorldCenter().x)/2;
	body[1] = (curBody->GetWorldCenter().y + curBody2->GetWorldCenter().y)/2;
	body[2] = (curBody->GetAngle() + curBody2->GetAngle())/2;

	env->SetFloatArrayRegion(ret,0,len,body);
	return ret;
}

void Java_com_example_playpalpengame_JarActivity_worldStep(void){
	float32 timeStep = 1.0f / FPS;
	int32 velocityIterations = 6;
	int32 positionIterations = 2;
	m_world->Step(timeStep, velocityIterations, positionIterations);
}


static int registerMethods(JNIEnv* env) {
 static const char* const kClassName = "com/example/playpalpengame/JarActivity";
 static JNINativeMethod gMethods[] = {{"initWorld", "()V", 		(void*) Java_com_example_playpalpengame_JarActivity_initWorld},
		 	 	 	 	 	 	 	 {"putIntoJar", "(I)Z",   	(void*) Java_com_example_playpalpengame_JarActivity_putIntoJar },
 	 	 	 	 	 	 	 	 	 {"updateAngle","(FFF)Z", 	(void*) Java_com_example_playpalpengame_JarActivity_updateAngle},
 	 	 	 	 	 	 	 	 	 {"getPosition","(I)[F",	(void*) Java_com_example_playpalpengame_JarActivity_getPosition},
 	 	 	 	 	 	 	 	 	 {"worldStep","()V",		(void*) Java_com_example_playpalpengame_JarActivity_worldStep}};
 jclass clazz;
 /* look up the class */
 clazz = env->FindClass(kClassName);
 if (clazz == NULL) {
  return -1;
 }

 /* register all the methods */
 if (env->RegisterNatives(clazz, gMethods,
   sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
  return -1;
 }



 /* fill out the rest of the ID cache */
 return 0;
}
/*
 * This is called by the VM when the shared library is first loaded.
 */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
 JNIEnv* env = NULL;
 jint result = -1;
 if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
  goto fail;
 }
 if (registerMethods(env) != 0) {
  goto fail;
 }
 /* success -- return valid version number */
 result = JNI_VERSION_1_4;
 fail: return result;
}
