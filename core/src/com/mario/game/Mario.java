package com.mario.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import java.util.*;

//Texture is a way to add a image to our app
public class Mario extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
	private Texture[] man;
	private int manState=0;
	private int pause = 0;
	private float gravity = 0.5f;
	private float velocity = 0;
	private float manY;
	private Texture coin;
	private Texture bomb;
	private int gameState;
	private Texture dizzyMan;
	private Sound coinCollect;
	private Sound gameOver;
	private Sound loopSound;

	private BitmapFont font;
	private int score;

	private Rectangle manRectangle;

	private ArrayList<Integer> coinXs ;
	private ArrayList<Integer> coinYs ;
	private ArrayList<Integer> bombXs ;
	private ArrayList<Integer> bombYs ;
	private ArrayList<Rectangle> coinRectangles;
	private ArrayList<Rectangle> bombRectangles;
	private int coinCount;
	private int bombCount ;

	private Random random;

	private void makeCoin(){
		float newY = random.nextFloat() * Gdx.graphics.getHeight();
		if(newY >= (Gdx.graphics.getHeight()-coin.getHeight())){
			newY -= coin.getHeight();
		}

		coinYs.add((int) newY);
		coinXs.add(Gdx.graphics.getWidth());

	}

	private void makeBomb(){
		float newY = random.nextFloat() * Gdx.graphics.getHeight();
		if(newY >= (Gdx.graphics.getHeight()-bomb.getHeight())){
			newY -= bomb.getHeight();
		}

		bombYs.add((int) newY);
		bombXs.add(Gdx.graphics.getWidth());

	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man=new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = (float) Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		coinXs=new ArrayList<>();
		coinYs=new ArrayList<>();
		bombXs=new ArrayList<>();
		bombYs=new ArrayList<>();
		random = new Random();
		coinRectangles = new ArrayList<>();
		bombRectangles = new ArrayList<>();
		coinCount = 0;
		bombCount=0;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);
		score=0;
		gameState=0;
		dizzyMan = new Texture("dizzy-1.png");
		coinCollect = Gdx.audio.newSound(Gdx.files.internal("coincollect.wav"));
		gameOver = Gdx.audio.newSound(Gdx.files.internal("gameOver.wav"));
		loopSound = Gdx.audio.newSound(Gdx.files.internal("loopSound.wav"));
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState == 1){
			//Game is live

			if(coinCount < 100){
				coinCount++;
			}else{
				coinCount=0;
				makeCoin();
			}

			if(bombCount<300){
				bombCount++;
			}else{
				bombCount=0;
				makeBomb();
			}

			coinRectangles.clear();
			for(int i=0;i<coinXs.size();i++){
				batch.draw(coin,coinXs.get(i), coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-6);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			bombRectangles.clear();
			for(int i=0;i<bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i), bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-11);
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			if(Gdx.input.justTouched()){
				if(manY<=(Gdx.graphics.getHeight()-man[manState].getHeight()))
					velocity =-20 ;
			}
			if(pause<8){
				pause++;
			}
			else {
				pause=0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;
			if(manY<=0){
				manY=0;
			}


		}else if(gameState == 0){
			//waiting to start
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}
		else if(gameState==2){
			//game over
			coinRectangles.clear();
			coinYs.clear();
			coinXs.clear();
			bombRectangles.clear();
			bombYs.clear();
			bombXs.clear();
			coinCount=0;
			bombCount=0;
			velocity=0;

			if(Gdx.input.justTouched()){
				gameState=1;
				score=0;
				manY=(float) Gdx.graphics.getHeight()/2;

			}
		}
		if(gameState==2){
			batch.draw(dizzyMan, (float) Gdx.graphics.getWidth() / 2 - (float) man[manState].getWidth() / 2, manY,man[manState].getWidth()-90,man[manState].getHeight()-120);
		}else {
			batch.draw(man[manState], (float) Gdx.graphics.getWidth() / 2 - (float) man[manState].getWidth() / 2, manY,man[manState].getWidth()-90,man[manState].getHeight()-120);
		}
		manRectangle = new Rectangle((float) Gdx.graphics.getWidth()/2 - (float)man[manState].getWidth()/2,manY,man[manState].getWidth()-90,man[manState].getHeight()-120);

		for(int i=0;i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;
				coinCollect.play();
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for(int i=0;i<bombRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				gameState=2;
				loopSound.stop();
				gameOver.play();
				break;
			}
		}
        font.draw(batch,String.valueOf(score),50,70);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
