/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package generictesting;

import org.athena.imis.diachron.archive.api.QueryLib;

/**
 *
 * @author Jim
 */
public class QueryLibTest {
    public static void main(String[] args) throws Exception {
        QueryLib q = new QueryLib();
        System.out.println(q.listDiachronicDatasets());
    }    
}
