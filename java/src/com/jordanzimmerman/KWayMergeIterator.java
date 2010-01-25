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
 * This is the interface to each of the buckets being merged
 *
 * @author Jordan Zimmerman (jordan@jordanzimmerman.com)
 * @version 1.1 Dec. 1, 2007 made a top level class
 */
public interface KWayMergeIterator<T extends KWayMergeIterator<T>>
{
    /**
     * Return true when this bucket is empty (NOTE: KWayMerge will call this multiple times so it's
     * best to have some sort of "done" flag)
     *
     * @return true/false
     * @throws KWayMergeError any errors
     */
    public boolean              isDone() throws KWayMergeError;

    /**
     * Advance to the next item in the bucket. The iterator starts at one before the first item, so this will
     * get called to move to the first item, etc. This method must be able to handle being called after {@link #isDone} returns
     * true.
     *
     * @throws KWayMergeError any errors
     */
    public void 			    advance() throws KWayMergeError;
                                          
    /**
     * Compare the current top item in this bucket to the top item in another bucket. Return the iterator with the
     * winning comparison (i.e. the smallest or whatever comparison is needed).
     *
     * @param iterator bucket to compare to
     * @return the winning bucket
     * @throws KWayMergeError any errors
     */
    public T                    compare(T iterator) throws KWayMergeError;
}
