//
//  HappinessViewController.swift
//  Happiness
//
//  Created by FranklinMichael on 15/08/15.
//  Copyright © 2015 Laminin. All rights reserved.
//

import UIKit

class HappinessViewController: UIViewController, FaceViewDataSource {

    private struct Constants {
        static let HappinessGestureScale: CGFloat = 4
    }
    
    var happiness: Int = 100 { // 0 is very sad, 100 is ecstatic
        didSet{
            happiness = min(max(happiness, 0), 100)
            print("Happiness = \(happiness)")
            updateUI()
        }
    }
    
    @IBAction func changeHappiness(gesture: UIPanGestureRecognizer) {
        switch gesture.state{
        case .Ended: fallthrough
        case .Changed:
            let translation = gesture.translationInView(faceView)
            let happinessChange = -Int(translation.y / Constants.HappinessGestureScale)
            
            if happinessChange != 0 {
                happiness += happinessChange
                gesture.setTranslation(CGPointZero, inView: faceView)
            }
        default: break
        }
    }
    
    @IBOutlet weak var faceView: FaceView! {
        didSet{
            faceView.dataSource  = self
            faceView.addGestureRecognizer(UIPinchGestureRecognizer(target: faceView, action: "scale:"))
//            faceView.addGestureRecognizer(UIPanGestureRecognizer(target: self, action: "changeHappiness:"))
        }
    }
    
    func smilinessForFaceView(sender: FaceView) -> Double {
        return Double (happiness - 50 ) / 50
    }
    
    func updateUI(){
        faceView.setNeedsDisplay()
    }
}
