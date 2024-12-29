package ao.co.isptec.aplm.binasjc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private VisualizarCiclistasMapa mActivity;
    private TelaMapa mActivity1;

    public SimWifiP2pBroadcastReceiver(VisualizarCiclistasMapa activity) {
        super();
        this.mActivity = activity;
    }

    public SimWifiP2pBroadcastReceiver(TelaMapa activity) {
        super();
        this.mActivity1 = activity;
    }

    public SimWifiP2pBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // This action is triggered when the Termite service changes state:
            // - creating the service generates the WIFI_P2P_STATE_ENABLED event
            // - destroying the service generates the WIFI_P2P_STATE_DISABLED event

            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {

                if(mActivity != null){
                    Toast.makeText(mActivity, "WiFi Direct enabled",
                            Toast.LENGTH_SHORT).show();
                }

                if(mActivity1 != null){
                    Toast.makeText(mActivity1, "WiFi Direct enabled",
                            Toast.LENGTH_SHORT).show();
                }

            } else {

                if(mActivity != null){
                    Toast.makeText(mActivity, "WiFi Direct disabled",
                            Toast.LENGTH_SHORT).show();
                }

                if(mActivity1 != null){
                    Toast.makeText(mActivity1, "WiFi Direct disabled",
                            Toast.LENGTH_SHORT).show();
                }

            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            if(mActivity != null){
                Toast.makeText(mActivity, "Peer list changed",
                        Toast.LENGTH_SHORT).show();
            }

            if(mActivity1 != null){
                Toast.makeText(mActivity1, "Peer list changed",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();

            if(mActivity != null){
                Toast.makeText(mActivity, "Network Membership changed",
                        Toast.LENGTH_SHORT).show();
            }

            if(mActivity1 != null){
                Toast.makeText(mActivity1, "Network Membership changed",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();

            if(mActivity != null){
                Toast.makeText(mActivity, "Group ownership changed",
                        Toast.LENGTH_SHORT).show();
            }

            if(mActivity1 != null){
                Toast.makeText(mActivity1, "Group ownership changed",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}