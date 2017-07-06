package com.spy.apollo.netty.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @version 1.0 2017-07-06 7:17
 * @since 1.0
 */
@Slf4j
public class ConsoleUtil {
    private void await() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals("quit")) {
                    scanner.close();
                }
            }
        } catch (Exception e) {
            log.error("scan exception", e);
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }
}
