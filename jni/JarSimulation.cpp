#include <jni.h>
#include <android/log.h>
#include <Box2D/Box2D.h>
#include <stdio.h>
#include <vector>

#define PTM_Ratio 500.f
#define FPS 60.f
#define Star_Size 90.f


#define X_middle 300
#define Y_middle 500
#define U_Boundary 825
#define D_Boundary 150

#define L_Boundary 20
#define R_Boundary 580


class Star{
	public:

	b2Body* bodies[2];

	Star(b2World* m_world, float xPos, float yPos, float radius, uint16 groupBits){
		int body_num = 2;
		int vertex_num = 3;

		for(int i=0; i<body_num; i++){
			b2Vec2 vertices[3];
			for(int j=0; j<vertex_num; j++){
				vertices[j].Set(radius*cos((j+0.5*i) * 2 * M_PI / vertex_num), radius*sin((j+0.5*i) * 2 * M_PI / vertex_num));
			}

			b2BodyDef starBodyDef;
			starBodyDef.type = b2_dynamicBody;
			starBodyDef.position.Set(xPos, yPos);
			starBodyDef.allowSleep = false;

			b2PolygonShape starShape;
			starShape.Set(vertices, vertex_num);

			b2FixtureDef starFixtureDef;
			starFixtureDef.shape = &starShape;
			starFixtureDef.density = 10;
			starFixtureDef.friction = 0.1f;
			starFixtureDef.restitution = 0.2f;
			starFixtureDef.filter.categoryBits = groupBits;
			starFixtureDef.filter.maskBits = groupBits;

			bodies[i] = m_world->CreateBody(&starBodyDef);
			bodies[i]->CreateFixture(&starFixtureDef);
		}

		b2WeldJointDef jointDef;
		jointDef.bodyA = bodies[0];
		jointDef.bodyB = bodies[1];
		jointDef.localAnchorA = bodies[0]->GetLocalCenter();
		jointDef.localAnchorB = bodies[1]->GetLocalCenter();
		b2RevoluteJoint* joint = (b2RevoluteJoint*)m_world->CreateJoint(&jointDef);
	}

	b2Vec2 getPosition(){
		float xPos = (bodies[0]->GetWorldCenter().x + bodies[1]->GetWorldCenter().x)/2;
		float yPos = (bodies[0]->GetWorldCenter().y + bodies[1]->GetWorldCenter().y)/2;

		char s[100];
		sprintf(s,"%f %f",xPos,yPos);
		//__android_log_write(ANDROID_LOG_DEBUG, "jar",s);

		if(xPos<L_Boundary/PTM_Ratio || xPos>R_Boundary/PTM_Ratio){
			//__android_log_write(ANDROID_LOG_DEBUG, "jar", "1:(");
			bodies[0]->SetTransform(b2Vec2(X_middle/PTM_Ratio, bodies[0]->GetWorldCenter().y), bodies[0]->GetAngle());
			bodies[1]->SetTransform(b2Vec2(X_middle/PTM_Ratio, bodies[1]->GetWorldCenter().y), bodies[1]->GetAngle());
		}
		if(yPos<D_Boundary/PTM_Ratio || yPos>U_Boundary/PTM_Ratio){
			//__android_log_write(ANDROID_LOG_DEBUG, "jar", "2:(");
			bodies[0]->SetTransform(b2Vec2(bodies[0]->GetWorldCenter().x, Y_middle/PTM_Ratio), bodies[0]->GetAngle());
			bodies[1]->SetTransform(b2Vec2(bodies[1]->GetWorldCenter().x, Y_middle/PTM_Ratio), bodies[1]->GetAngle());
		}

		xPos = (bodies[0]->GetWorldCenter().x + bodies[1]->GetWorldCenter().x)/2;
		yPos = (bodies[0]->GetWorldCenter().y + bodies[1]->GetWorldCenter().y)/2;

		return b2Vec2(xPos,yPos);
	}

	float getAngle(){
		return (bodies[0]->GetAngle() + bodies[1]->GetAngle())/2;
	}

};


uint16 Group[5] = {0x0001, 0x0002, 0x0004, 0x0008, 0x0010};

b2World* m_world;
std::vector< Star* > starBodies;

void generateStarBody(int _x, int _y, int _g){

	Star* newStar = new Star(m_world, _x, _y, Star_Size/PTM_Ratio/2, Group[_g]);
	starBodies.push_back(newStar);
	return;
}

void init() {

	b2Vec2 gravity(0.0f, -100.0f);
	m_world = new b2World(gravity);
	starBodies.clear();


	b2BodyDef groundBodyDef;
	b2EdgeShape edgeShape;
	b2FixtureDef groundFixtureDef;
	b2PolygonShape polygonShape;

	groundBodyDef.type = b2_staticBody;
	groundFixtureDef.friction = 0.1f;
	groundFixtureDef.restitution = 0.2f;
	groundFixtureDef.shape = &edgeShape;
	groundFixtureDef.filter.categoryBits = Group[0] | Group[1] | Group[2] | Group[3] | Group[4];

	edgeShape.Set( b2Vec2(L_Boundary/PTM_Ratio,U_Boundary/PTM_Ratio), b2Vec2(R_Boundary/PTM_Ratio,U_Boundary/PTM_Ratio) );//U
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(L_Boundary/PTM_Ratio,D_Boundary/PTM_Ratio), b2Vec2(R_Boundary/PTM_Ratio,D_Boundary/PTM_Ratio) );//D
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(L_Boundary/PTM_Ratio,D_Boundary/PTM_Ratio), b2Vec2(L_Boundary/PTM_Ratio,U_Boundary/PTM_Ratio) );//L
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);

	edgeShape.Set( b2Vec2(R_Boundary/PTM_Ratio,D_Boundary/PTM_Ratio), b2Vec2(R_Boundary/PTM_Ratio,U_Boundary/PTM_Ratio) );//R
	m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);


	b2Vec2 P[6] = { b2Vec2(L_Boundary/PTM_Ratio, (U_Boundary-150.f)/PTM_Ratio),
					b2Vec2(120.f/PTM_Ratio, 775.f/PTM_Ratio),
					b2Vec2(120.f/PTM_Ratio, U_Boundary/PTM_Ratio),
					b2Vec2(480.f/PTM_Ratio, U_Boundary/PTM_Ratio),
					b2Vec2(480.f/PTM_Ratio, 775.f/PTM_Ratio),
					b2Vec2(R_Boundary/PTM_Ratio, (U_Boundary-150.f)/PTM_Ratio)};



	for(int i=1; i<6; i++){
		edgeShape.Set( P[i-1], P[i] );
		m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);
	}

	b2Vec2 P2[4] ={	b2Vec2(L_Boundary/PTM_Ratio, 200.f/PTM_Ratio),
					b2Vec2(120.f/PTM_Ratio, D_Boundary/PTM_Ratio),
					b2Vec2(480.f/PTM_Ratio, D_Boundary/PTM_Ratio),
					b2Vec2(R_Boundary/PTM_Ratio, 200.f/PTM_Ratio)};

	for(int i=1; i<4; i++){
			edgeShape.Set( P2[i-1], P2[i] );
			m_world->CreateBody(&groundBodyDef)->CreateFixture(&groundFixtureDef);
		}
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
		JNIEnv* env, jobject thiz, jint layerIndex) {

	generateStarBody(X_middle/PTM_Ratio, Y_middle/PTM_Ratio, layerIndex);
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
	jfloatArray ret = env->NewFloatArray(len);
	jfloat *body = new jfloat[len];

	Star* curStar = starBodies.at(idx);
	b2Vec2 curPos = curStar->getPosition();
	float xPos = curPos.x;
	float yPos = curPos.y;
	float angle = curStar->getAngle();

	body[0] = xPos;
	body[1] = yPos;
	body[2] = angle;

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
