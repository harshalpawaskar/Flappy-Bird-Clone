package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bgImg;
	//ShapeRenderer shapeRenderer;

	//Bird Related Parameters
	Texture[] birds;
	Texture bottomTube,topTube;
	int flapState = 0, gameState = 0, interval = 0;
	float velocity = 0, gravity = 1.25f;
	float birdY = 0;

	//Tube Related Parameters
	float gap = 400;
	Random randomGenerator;
	float tubeVelocity = 6;
	int numberOfTubes = 4;
	float distanceBetweenTubes;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];

	//Collision
	Circle birdCircle;
	Rectangle[] topTubeRectangles = new Rectangle[numberOfTubes];
	Rectangle[] bottomTubeRectangles = new Rectangle[numberOfTubes];

	//Score
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	//GameOver
	Texture gameOverImg;


	@Override
	public void create () {
		batch = new SpriteBatch();
		bgImg = new Texture("bg.png");
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();


		birds = new Texture[2];
		birds[0] = new Texture("bird1.png");
		birds[1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;

		bottomTube = new Texture("bottomtube.png");
		topTube = new Texture("toptube.png");
		randomGenerator = new Random();

		distanceBetweenTubes = Gdx.graphics.getWidth()*3/4;

		for(int i=0;i<numberOfTubes;i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth()/2 - bottomTube.getWidth()/2 +  Gdx.graphics.getWidth() + i*distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameOverImg = new Texture("game-over.png");
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(bgImg, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1){

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				scoringTube = (scoringTube + 1) % numberOfTubes;
				Gdx.app.log("Score",String.valueOf(score) + " ScoringTube :" + String.valueOf(scoringTube));
			}

			if(Gdx.input.justTouched()){
				velocity = -25;
			}

			for(int i=0;i<numberOfTubes;i++) {

				if(tubeX[i] < -bottomTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]);
				bottomTubeRectangles[i].set(tubeX[i],Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				topTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			}

			if(birdY>0){
				velocity = velocity + gravity;
				birdY-=velocity;
			}else {
				gameState = 2;
			}
		}else if(gameState==0) {
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if(gameState==2){
			batch.draw(gameOverImg,Gdx.graphics.getWidth()/2 - gameOverImg.getWidth() * 0.75f/2,Gdx.graphics.getHeight()/2 - gameOverImg.getHeight() * 0.75f/2,gameOverImg.getWidth() * 0.75f,gameOverImg.getHeight() * 0.75f);

			if(Gdx.input.justTouched()){
				gameState = 1;
				birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;
				for(int i=0;i<numberOfTubes;i++){
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
					tubeX[i] = Gdx.graphics.getWidth()/2 - bottomTube.getWidth()/2 +  Gdx.graphics.getWidth() + i*distanceBetweenTubes;

					topTubeRectangles[i] = new Rectangle();
					bottomTubeRectangles[i] = new Rectangle();
				}
				scoringTube = 0;
				score = 0;
				velocity = 0;
			}
		}

		if(gameState==1) {
			if (interval < 5) {
				interval++;
			} else {
				if (flapState == 1) {
					flapState = 0;
				} else {
					flapState = 1;
				}
				interval = 0;
			}
		}

		batch.draw(birds[flapState],(float)(Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2),birdY);
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for(int i=0;i<numberOfTubes;i++){
			//shapeRenderer.rect(bottomTubeRectangles[i].x,bottomTubeRectangles[i].y,bottomTubeRectangles[i].getWidth(),bottomTubeRectangles[i].getHeight());
			//shapeRenderer.rect(topTubeRectangles[i].x,topTubeRectangles[i].y,topTubeRectangles[i].getWidth(),topTubeRectangles[i].getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				Gdx.app.log("Collision","YES!!!");
				gameState = 2;
			}
		}

		//shapeRenderer.end();
	}
}
