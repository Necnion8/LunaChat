/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.util.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * プレイヤー辞書変換の設定コマンド
 * @author ucchy
 */
public class LunaChatDictionaryCommand {

    private static final String PERM_DICTIONARY = "lunachat.dictionary";

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 実行したかどうか（falseを返した場合、サーバーがUsageを表示する）
     */
    public boolean execute(ChannelMember sender, String label, String[] args) {

        // 引数が無ければ、usageを表示して終了する
        if (args.length == 0) {
            printUsage(sender, label);
            return true;
        }

        if ( !args[0].equalsIgnoreCase("add") &&
                !args[0].equalsIgnoreCase("remove") &&
                !args[0].equalsIgnoreCase("view") ) {
            sender.sendMessage(Messages.errmsgCommand());
            printUsage(sender, label);
            return true;
        }

        LunaChatAPI api = LunaChat.getAPI();
        if ( args[0].equalsIgnoreCase("add") ) {

            // addの場合は、さらに2つ引数が必要
            if ( args.length <= 2 ) {
                sender.sendMessage(Messages.errmsgCommand());
                printUsage(sender, label);
                return true;
            }

            String key = args[1];
            String value = args[2];
            api.setPlayerDictionary(sender.toString(), key, value);

            sender.sendMessage(Messages.cmdmsgDictionaryAdd(key, value));
            return true;

        } else if ( args[0].equalsIgnoreCase("remove") ) {

            // removeの場合は、さらに1つ引数が必要
            if (args.length == 1) {
                sender.sendMessage(Messages.errmsgCommand());
                printUsage(sender, label);
                return true;
            }

            String key = args[1];
            api.removePlayerDictionary(sender.toString(), key);

            sender.sendMessage(Messages.cmdmsgDictionaryRemove(key));
            return true;

        } else if ( args[0].equalsIgnoreCase("view") ) {

            Map<String, String> dic = api.getPlayerAllDictionary(sender.toString());
            for ( String key : dic.keySet() ) {
                String value = dic.get(key);
                sender.sendMessage(key + ChatColor.GRAY + " -> " + ChatColor.WHITE + value);
            }
            return true;

        }

        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     * @param sender
     * @param label
     */
    private void printUsage(ChannelMember sender, String label) {
        sender.sendMessage(Messages.usagePlayerDictionary(label));
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender TABキー補完の実行者
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     */
    public List<String> onTabComplete(ChannelMember sender, String label, String[] args) {
        if ( args.length == 1 ) {
            String arg = args[0].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : new String[]{"add", "remove", "view"} ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("remove") ) {
            // 辞書に登録されているワードで補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name :
                    LunaChat.getAPI().getPlayerAllDictionary(sender.toString()).keySet() ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;
        }
        return new ArrayList<>();
    }
}
