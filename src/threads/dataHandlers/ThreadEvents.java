/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package threads.dataHandlers;

import robot.Robot;
import table.Table;
import threads.AbstractThread;
import utils.Sleep;

import java.util.LinkedList;

/**
 *  Gestionnaire des events LL
 *  @author discord
 */
public class ThreadEvents extends AbstractThread
{

    Table table;

    Robot robot;

    LinkedList<String> events;

    public ThreadEvents(Table table, Robot robot, ThreadSerial serial)
    {
        this.table = table;
        this.robot = robot;
        events = serial.getEventBuffer();

    }

    @Override
    public void run()
    {
        String event = null;
        Thread.currentThread().setPriority(6);
        while(!ThreadSerial.shutdown)
        {

            Sleep.sleep(100);

            if(events.peek() != null)
                event = events.poll();

            if(event == null)
                continue;

            //==========

            // TODO Events et réactions

            //==========

            event = null;
        }

    }
}
