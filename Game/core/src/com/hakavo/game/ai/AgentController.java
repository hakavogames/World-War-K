package com.hakavo.game.ai;

import com.hakavo.ineffable.core.*;
import com.badlogic.gdx.utils.Queue;

public class AgentController extends GameComponent {
    protected Queue<Task> tasks=new Queue<Task>();
    protected Task lastTask;
    public boolean running=true;
    
    @Override
    public void update(float delta) {
        if(running&&tasks.size>0) {
            Task task=tasks.first();
            
            if(task!=lastTask)task.onTaskAssigned();
            if(task.isActive())
                task.onTaskPerform(delta);
            else if(task.isComplete())
                tasks.removeFirst();
            
            lastTask=task;
        }
    }
    @Override
    public void start() {
    }
    
    public void assignTask(Task task) {
        task.init(this);
        tasks.addFirst(task);
    }
}
