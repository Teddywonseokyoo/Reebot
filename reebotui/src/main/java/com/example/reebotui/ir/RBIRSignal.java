package com.example.reebotui.ir;

/**
 * Created by freem on 2017-03-09.
 */

public enum RBIRSignal {

    SAMSUNGTV_power{
        @Override
        public String toString() {
            return "0xE0E040BF";
        }
    },
    SAMSUNGTV_input{
        @Override
        public String toString() {
            return "0xE0E0807F";
        }
    },
    SAMSUNGTV_ch0{
        @Override
        public String toString() {
            return "0xE0E08877";
        }
    },
    SAMSUNGTV_ch1{
        @Override
        public String toString() {
            return "0xE0E020DF";
        }
    },
    SAMSUNGTV_ch2{
        @Override
        public String toString() {
            return "0xE0E0A05F";
        }
    },
    SAMSUNGTV_ch3{
        @Override
        public String toString() {
            return "0xE0E0609F";
        }
    },
    SAMSUNGTV_ch4{
        @Override
        public String toString() {
            return "0xE0E010EF";
        }
    },
    SAMSUNGTV_ch5{
        @Override
        public String toString() {
            return "0xE0E0906F";
        }
    },
    SAMSUNGTV_ch6{
        @Override
        public String toString() {
            return "0xE0E050AF";
        }
    },
    SAMSUNGTV_ch7{
        @Override
        public String toString() {
            return "0xE0E030CF";
        }
    },
    SAMSUNGTV_ch8{
        @Override
        public String toString() {
            return "0xE0E0B04F";
        }
    },
    SAMSUNGTV_ch9{
        @Override
        public String toString() {
            return "0xE0E0708F";
        }
    },
    LGTV_power{
        @Override
        public String toString() {
            return "20DF10EF";
        }
    },
    LGTV_input{
        @Override
        public String toString() {
            return "20DFD02F";
        }
    },
    LGTV_ch0{
        @Override
        public String toString() {
            return "20DF08F7";
        }
    },
    LGTV_ch1{
        @Override
        public String toString() {
            return "20DF8877";
        }
    },
    LGTV_ch2{
        @Override
        public String toString() {
            return "20DF48B7";
        }
    },
    LGTV_ch3{
        @Override
        public String toString() {
            return "20DFC837";
        }
    },
    LGTV_ch4{
        @Override
        public String toString() {
            return "20DF28D7";
        }
    },
    LGTV_ch5{
        @Override
        public String toString() {
            return "20DFA857";
        }
    },
    LGTV_ch6{
        @Override
        public String toString() {
            return "20DF6897";
        }
    },
    LGTV_ch7{
        @Override
        public String toString() {
            return "20DFE817";
        }
    },
    LGTV_ch8{
        @Override
        public String toString() {
            return "20DF18E7";
        }
    },
    LGTV_ch9{
        @Override
        public String toString() {
            return "20DF9867";
        }
    },
    LG_enter {
        @Override
        public String toString() {
            return "5B0322DD";
        }
    },
    LG_power{
        @Override
        public String toString() {
            return "5B0310EF";
        }
    },
    LG_chup {
        @Override
        public String toString() {
            return "5B0300FF";
        }
    },
    LG_chdown {
        @Override
        public String toString() {
            return "5B03807F";
        }
    },
    LG_volup {
        @Override
        public String toString() { return "5B0340BF"; }
    },
    LG_voldown {
        @Override
        public String toString() {
            return "5B03C03F";
        }
    },
    LG_mute {
        @Override
        public String toString() {
            return "5B03906F";
        }
    },
    LG_ch0 {
        @Override
        public String toString() {
            return "5B0308F7";
        }
    },
    LG_ch1 {
        @Override
        public String toString() {
            return "5B038877";
        }
    },
    LG_ch2 {
        @Override
        public String toString() {
            return "5B0348B7";
        }
    },
    LG_ch3 {
        @Override
        public String toString() {
            return "5B03C837";
        }
    },
    LG_ch4 {
        @Override
        public String toString() {
            return "5B0328D7";
        }
    },
    LG_ch5 {
        @Override
        public String toString() {
            return "5B03A857";
        }
    },
    LG_ch6 {
        @Override
        public String toString() {
            return "5B036897";
        }
    },
    LG_ch7 {
        @Override
        public String toString() {
            return "5B03E817";
        }
    },
    LG_ch8 {
        @Override
        public String toString() {
            return "5B0318E7";
        }
    },
    LG_ch9 {
        @Override
        public String toString() {
            return "5B039867";
        }
    },
    LG_bch {
        @Override
        public String toString() {
            return "5B0332CD";
        }
    },
    SK_enter {
        @Override
        public String toString() {
            return "1FE629D";
        }
    },
    SK_power{
        @Override
        public String toString() {
            return "1FE807F";
        }
    },
    SK_chup {
        @Override
        public String toString() {
            return "1FE02FD";
        }
    },
    SK_chdown {
        @Override
        public String toString() {
            return "1FE827D";
        }
    },
    SK_volup {
        @Override
        public String toString() {
            return "1FEC23D";
        }
    },
    SK_mute {
        @Override
        public String toString() {
            return "1FEA25D";
        }
    },
    SK_voldown {
        @Override
        public String toString() {
            return "1FE42BD";
        }
    },
    SK_ch0 {
        @Override
        public String toString() {
            return "1FE04FB";
        }
    },
    SK_ch1 {
        @Override
        public String toString() {
            return "1FE847B";
        }
    },
    SK_ch2 {
        @Override
        public String toString() {
            return "1FE44BB";
        }
    },
    SK_ch3 {
        @Override
        public String toString() {
            return "1FEC43B";
        }
    },
    SK_ch4 {
        @Override
        public String toString() {
            return "1FE24DB";
        }
    },
    SK_ch5 {
        @Override
        public String toString() {
            return "1FEA45B";
        }
    },
    SK_ch6 {
        @Override
        public String toString() {
            return "1FE649B";
        }
    },
    SK_ch7 {
        @Override
        public String toString() {
            return "1FEE41B";
        }
    },
    SK_ch8 {
        @Override
        public String toString() {
            return "1FE14EB";
        }
    },
    SK_ch9 {
        @Override
        public String toString() {
            return "1FE946B";
        }
    },
    SK_bch {
        @Override
        public String toString() {
            return "1FE728D";
        }
    },
    KT_enter {
        @Override
        public String toString() { return "9CA8C837";}
    },
    KT_power{
        @Override
        public String toString() {
            return "9CA800FF";
        }
    },
    KT_chup {
        @Override
        public String toString() {
            return "9CA8926D";
        }
    },
    KT_chdown {
        @Override
        public String toString() {
            return "9CA852AD";
        }
    },
    KT_volup {
        @Override
        public String toString() { return "9CA8F807"; }
    },
    KT_voldown {
        @Override
        public String toString() {
            return "9CA802FD";
        }
    },
    KT_mute {
        @Override
        public String toString() {
            return "9CA818E7";
        }
    },
    KT_ch0 {
        @Override
        public String toString() {
            return "9CA830CF";
        }
    },
    KT_ch1 {
        @Override
        public String toString() {
            return "9CA8C03F";
        }
    },
    KT_ch2 {
        @Override
        public String toString() {
            return "9CA820DF";
        }
    },
    KT_ch3 {
        @Override
        public String toString() {
            return "9CA8A05F";
        }
    },
    KT_ch4 {
        @Override
        public String toString() {
            return "9CA8609F";
        }
    },
    KT_ch5 {
        @Override
        public String toString() {
            return "9CA8E01F";
        }
    },
    KT_ch6 {
        @Override
        public String toString() {
            return "9CA810EF";
        }
    },
    KT_ch7 {
        @Override
        public String toString() {
            return "9CA8906F";
        }
    },
    KT_ch8 {
        @Override
        public String toString() {
            return "9CA850AF";
        }
    },
    KT_ch9 {
        @Override
        public String toString() {
            return "9CA8D02F";
        }
    },
    KT_bch {
        @Override
        public String toString() {
            return "9CA88679";
        }
    },
    DLIVE1_enter {
        @Override
        public String toString() {
            return "5F807689";
        }
    },
    DLIVE1_power{
        @Override
        public String toString() {
            return "5F800AF5";
        }
    },
    DLIVE1_chup {
        @Override
        public String toString() {
            return "5B0300FF";
        }
    },
    DLIVE1_chdown {
        @Override
        public String toString() {
            return "5B03807F";
        }
    },
    DLIVE1_volup {
        @Override
        public String toString() { return "5F80817E"; }
    },
    DLIVE1_voldown {
        @Override
        public String toString() {
            return "5F8041BE";
        }
    },
    DLIVE1_mute {
        @Override
        public String toString() {
            return "5F80E11E";
        }
    },
    DLIVE1_ch0 {
        @Override
        public String toString() {
            return "5F80D926";
        }
    },
    DLIVE1_ch1 {
        @Override
        public String toString() {
            return "5F8049B6";
        }
    },
    DLIVE1_ch2 {
        @Override
        public String toString() {
            return "5F80C936";
        }
    },
    DLIVE1_ch3 {
        @Override
        public String toString() {
            return "5F8029D6";
        }
    },
    DLIVE1_ch4 {
        @Override
        public String toString() {
            return "5F80A956";
        }
    },
    DLIVE1_ch5 {
        @Override
        public String toString() {
            return "5F806996";
        }
    },
    DLIVE1_ch6 {
        @Override
        public String toString() {
            return "5F80E916";
        }
    },
    DLIVE1_ch7 {
        @Override
        public String toString() {
            return "5F8019E6";
        }
    },
    DLIVE1_ch8 {
        @Override
        public String toString() {
            return "5F809966";
        }
    },
    DLIVE1_ch9 {
        @Override
        public String toString() {
            return "5F8059A6";
        }
    },
    DLIVE1_bch {
        @Override
        public String toString() {
            return "5F808E71";
        }
    }

}
