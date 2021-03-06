/**
 * Copyright © 2012 Akiban Technologies, Inc.  All rights
 * reserved.
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This program may also be available under different license terms.
 * For more information, see www.akiban.com or contact
 * licensing@akiban.com.
 *
 * Contributors:
 * Akiban Technologies, Inc.
 */

/* The original from which this derives bore the following: */

/*

   Derby - Class org.apache.derby.impl.sql.compile.DMLModStatementNode

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package com.akiban.sql.parser;

import com.akiban.sql.StandardException;

/**
 * A DMLStatement for a table modification: to wit, INSERT
 * UPDATE or DELETE.
 *
 */

public abstract class DMLModStatementNode extends DMLStatementNode
{
    protected FromVTI targetVTI;
    protected TableName targetTableName;
    protected ResultColumnList returningColumnList;
    private int statementType;

    /**
     * Initializer for a DMLModStatementNode -- delegate to DMLStatementNode
     *
     * @param resultSet A ResultSetNode for the result set of the
     *                                  DML statement
     */
    public void init(Object resultSet) {
        super.init(resultSet);
        statementType = getStatementType();
    }

    /**
     * Initializer for a DMLModStatementNode -- delegate to DMLStatementNode
     *
     * @param resultSet A ResultSetNode for the result set of the
     *                                  DML statement
     * @param statementType used by nodes that allocate a DMLMod directly
     *                                          (rather than inheriting it).
     */
    public void init(Object resultSet, Object statementType) {
        super.init(resultSet);
        this.statementType = ((Integer)statementType).intValue();
    }

    /**
     * Fill this node with a deep copy of the given node.
     */
    public void copyFrom(QueryTreeNode node) throws StandardException {
        super.copyFrom(node);

        DMLModStatementNode other = (DMLModStatementNode)node;
        this.targetVTI = (FromVTI)getNodeFactory().copyNode(other.targetVTI,
                                                            getParserContext());
        this.targetTableName = (TableName)getNodeFactory().copyNode(other.targetTableName,
                                                                    getParserContext());
        this.statementType = other.statementType;
        
        this.returningColumnList = (ResultColumnList)getNodeFactory()
                .copyNode(other.returningColumnList, getParserContext());
    }

    void setTarget(QueryTreeNode targetName) {
        if (targetName instanceof TableName) {
            this.targetTableName = (TableName)targetName;
        }
        else {
            this.targetVTI = (FromVTI)targetName;
            targetVTI.setTarget();
        }
    }

    /**
     *
     * INSERT/UPDATE/DELETE are always atomic.
     *
     * @return true 
     */
    public boolean isAtomic() {
        return true;
    }

    public TableName getTargetTableName() {
        return targetTableName;
    }

    public ResultColumnList getReturningList() {
        return this.returningColumnList;
    }

    public void setReturningList(ResultColumnList returningColumnList) {
        this.returningColumnList = returningColumnList;
    }

    /**
     * Prints the sub-nodes of this object.  See QueryTreeNode.java for
     * how tree printing is supposed to work.
     *
     * @param depth The depth of this node in the tree
     */

    public void printSubNodes(int depth) {
        super.printSubNodes(depth);

        printLabel(depth, "targetTableName: ");
        targetTableName.treePrint(depth + 1);

        if (returningColumnList != null) {
            printLabel(depth, "returningList: ");
            returningColumnList.treePrint(depth+1);
        }
    }

    /**
     * Accept the visitor for all visitable children of this node.
     * 
     * @param v the visitor
     *
     * @exception StandardException on error
     */
    void acceptChildren(Visitor v) throws StandardException {
        super.acceptChildren(v);

        if (targetTableName != null) {
            targetTableName.accept(v);
        }
        if (returningColumnList != null) {
            returningColumnList.accept(v);
        }
    }
}
