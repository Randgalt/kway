/*
 * Copyright 2008-2010 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jordanzimmerman;

/**
 * Wrapper for any exceptions thrown
 * 
 * @author Jordan Zimmerman (jordan@jordanzimmerman.com)
 * @version 1.1 Dec. 1, 2007 made a top level class
 */
public class KWayMergeError extends Exception
{
    public KWayMergeError()
    {
        fCause = null;
    }

    public KWayMergeError(String message)
    {
        super(message);
        fCause = null;
    }

    public KWayMergeError(String message, Throwable cause)
    {
        super(message);
        fCause = cause;
    }

    public KWayMergeError(Throwable cause)
    {
        fCause = cause;
    }

    public Throwable getCause()
    {
        return fCause;
    }

    private Throwable fCause;
}
