package com.tyoku.map;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import com.tyoku.BattleRoyale;
import com.tyoku.util.BRUtils;

/**
 * ゲーム用のマップ絵画
 * @author tyoku
 *
 */
public class BrMapRender extends MapRenderer {
        private BattleRoyale plugin;

        public BrMapRender(BattleRoyale plugin) {
                this.plugin = plugin;
        }

        @Override
        public void render(MapView paramMapView, MapCanvas paramMapCanvas, Player paramPlayer) {
                //格子絵画
                int canvassize = 128;
                int padding = 10;
                byte lineColor = MapPalette.LIGHT_GRAY;
                for(int i = 0; i < canvassize; i+=padding){
                        for(int j = 0; j < canvassize; j++){
                                paramMapCanvas.setPixel( i, j, lineColor );
                                paramMapCanvas.setPixel( j, i, lineColor );
                        }
                }
                //デッド予告ゾーンを黄緑塗り
                BRUtils.drawBRBapBlocks(paramMapCanvas, plugin.getNextAreaBlocks(), MapPalette.matchColor(Color.YELLOW));

                //デッドゾーンを赤塗り
                BRUtils.drawBRBapBlocks(paramMapCanvas, plugin.getDeadAreaBlocks(), MapPalette.RED);

                //建造物赤塗り
                BRUtils.drawBrbuild(paramMapCanvas, plugin, MapPalette.RED);

                //座標文字列絵画
                int count = 0;
                String[] numary = new String[]{"0","1","2","3","4","5","6","7","8","9","0","1","2","3"};
                String[] alphaary = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n"};
                for(int i = 0; i < canvassize; i+=padding){
                        paramMapCanvas.drawText(i+4, 0, MinecraftFont.Font, numary[count]);
                        paramMapCanvas.drawText(0, i+2, MinecraftFont.Font, alphaary[count]);
                        count++;
                }

                //Scale.FARで大体1マス80x80ブロック

        }
}